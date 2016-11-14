package io.sealights.plugins.sealightsjenkins.integration.upgrade;

import io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.*;
import io.sealights.plugins.sealightsjenkins.utils.ArchiveUtils;
import io.sealights.plugins.sealightsjenkins.utils.FileAndFolderUtils;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.PathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.ComponentName.BUILD_SCANNER_COMPONENT_NAME;
import static io.sealights.plugins.sealightsjenkins.integration.upgrade.entities.ComponentName.TEST_LISTENER_COMPONENT_NAME;

public abstract class AbstractUpgradeManager {

    private UpgradeProxy upgradeProxy;
    protected UpgradeConfiguration upgradeConfiguration;
    protected final Logger logger;
    private static final int VERSIONS_HISTORY_TO_SAVE_BESIDE_RECOMMENDED = 2;

    public static final String SL_RELATIVE_CACHE_FOLDER = "sl-cache";
    public static final String MAVEN_PLUGIN_AGENTS_RELATIVE_CACHE_FOLDER = "maven-plugin";
    protected String embeddedVersion;

    public AbstractUpgradeManager(UpgradeProxy upgradeProxy, UpgradeConfiguration upgradeConfiguration, Logger logger) {
        this.upgradeProxy = upgradeProxy;
        this.upgradeConfiguration = upgradeConfiguration;
        this.logger = logger;
        this.embeddedVersion = AbstractUpgradeManager.class.getPackage().getImplementationVersion();
    }

    /**
     * Ensures that the latest version of the agent is present locally (downloads it if needed)
     *
     * @return the path to the JAR file
     */
    public String ensureLatestAgentPresentLocally() {
        try {
            UpgradeResponse upgradeResponse = upgradeProxy.getRecommendedVersion("sealights-java");
            if (!isValidResponse(upgradeResponse)) {
                throw new Exception("Could not get latest version info from server. " +
                        "The response from the server has invalid fields.");
            }

            String jarsFolder = PathUtils.join(upgradeConfiguration.getFilesStorage(),
                    SL_RELATIVE_CACHE_FOLDER, MAVEN_PLUGIN_AGENTS_RELATIVE_CACHE_FOLDER);
            if (!FileAndFolderUtils.verifyFolderExists(jarsFolder)) {
                throw new Exception("Could not verify that cache folder exists: '" + jarsFolder + "'.");
            }

            String foundAgent = findOrDownloadAgentByVersion(jarsFolder, upgradeResponse);
            return foundAgent;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error while trying to resolve Sealights recommended agents version. " +
                    "Probably the server did not found recommended agents version.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to resolve Sealights recommended agents version.", e);
        }
    }

    /*
    * Response will be valid if:
    * 1. Version is in the right format.
    * 2. The agent name can be found in the ComponentName enum.
    * We don't check if url is malformed because maybe download won't be needed.
    * */
    private boolean isValidResponse(UpgradeResponse upgradeResponse) {
        boolean isValid = true;
        if (upgradeResponse == null) {
            logger.warning("Got response 'null'.");
            return false;
        }

        AgentInfo agentInfo = upgradeResponse.getAgent();
        if (agentInfo == null) {
            logger.warning("Got response with agent information 'null'.");
            return false;
        }

        if (!Version.isValidVersion(agentInfo.getVersion())) {
            logger.warning("Got response with invalid version '" + agentInfo.getVersion() + "'.");
            isValid = false;
        }

        if (!isValidAgentName(agentInfo.getName())) {
            logger.warning("Got response with invalid agent name '" + agentInfo.getName() + "'.");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidAgentName(String name) {
        return "sealights-java".equals(name);
    }

    private void rearrangeJarsFolder(String jarsFolder) {
        File folder = new File(jarsFolder);
        File[] filesInFolder = folder.listFiles();
        if (filesInFolder == null) {
            logger.warning("Unable to rearrange the cache folder since 'filesInFolder' is null.");
            return;
        }
        TreeMap<Version, List<File>> versions = createAgentsVersionsSortedMap(filesInFolder);
        if (versions.size() > VERSIONS_HISTORY_TO_SAVE_BESIDE_RECOMMENDED) {
            performJarsRearrangement(versions);
        }
    }

    private void performJarsRearrangement(TreeMap<Version, List<File>> versions) {
        versions = getVersionsToDelete(versions);
        Iterator it = versions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Version, List<File>> pair = (Map.Entry<Version, List<File>>) it.next();
            List<File> agentsToDelete = pair.getValue();
            for (File agent : agentsToDelete)
                tryDeleteFile(agent, "old agent");
        }
    }

    private TreeMap<Version, List<File>> getVersionsToDelete(TreeMap<Version, List<File>> versions) {
        for (int i = 0; i < VERSIONS_HISTORY_TO_SAVE_BESIDE_RECOMMENDED; i++) {
            versions.remove(versions.lastKey());
        }
        return versions;
    }

    private TreeMap<Version, List<File>> createAgentsVersionsSortedMap(File[] filesInFolder) {
        TreeMap<Version, List<File>> versionsTree = new TreeMap<>();
        for (File f : filesInFolder) {

            if (f == null || !f.isFile())
                continue;

            String filename = f.getName();
            boolean isAgentJar = filename.startsWith("sl-") && filename.endsWith(".jar");
            if (!isAgentJar)
                continue;

            String detectedVersion = detectVersionFromAgentFileName(filename);
            if (!Version.isValidVersion(detectedVersion)) {
                continue;
            }
            Version v = new Version(detectedVersion);
            addAgentToVersionAgentTree(versionsTree, v, f);
        }

        return versionsTree;
    }

    private String detectVersionFromAgentFileName(String fileName) {
        fileName = fileName.replace(BUILD_SCANNER_COMPONENT_NAME.toString() + "-", "");
        fileName = fileName.replace(TEST_LISTENER_COMPONENT_NAME.toString() + "-", "");
        return fileName.replace(".jar", "");
    }

    private void addAgentToVersionAgentTree(TreeMap<Version, List<File>> versionsTree, Version version, File agent) {
        List<File> listToAdd = tryGetMatchingVersionInVersionAgentTree(versionsTree, version);

        if (listToAdd != null) {
            listToAdd.add(agent);
        } else {
            //create new version key
            List<File> filesRelatedToVersion = new ArrayList<>();
            filesRelatedToVersion.add(agent);
            versionsTree.put(version, filesRelatedToVersion);
        }
    }

    private List<File> tryGetMatchingVersionInVersionAgentTree(TreeMap<Version, List<File>> versionsTree, Version version) {
        for (Version v : versionsTree.keySet()) {
            if (v.equals(version))
                return versionsTree.get(v);
        }
        return null;
    }

    /**
     * Based on (@link upgradeResponse), checks if the file exists. If it doesn't, it downloads the file.
     *
     * @param jarsFolder      Cache folder where agents are stored
     * @param upgradeResponse
     * @return Agent Jar file path
     * @throws IOException
     */
    protected String findOrDownloadAgentByVersion(String jarsFolder, UpgradeResponse upgradeResponse) throws IOException {

        String jarFile;
        AgentInfo agentInfo = upgradeResponse.getAgent();
        String recommendedVersion = agentInfo.getVersion();

        logger.info("Checking if agent '" + getComponentName()
                + " with version '" + recommendedVersion + "' exists in files storage.");

        jarFile = tryGetRecommendedAgentFromFolder(jarsFolder, recommendedVersion);
        if (jarFile != null) {
            logger.info("Found '" + getComponentName()
                    + "' with version '" + recommendedVersion + "' in '" + jarFile + "'.");
            return jarFile;
        }

        logger.info("Trying to get agent '" + getComponentName()
                + "' with version '" + recommendedVersion + "' from remote storage.");
        jarFile = tryGetRecommendedAgentFromServer(jarsFolder, upgradeResponse);
        if (jarFile != null) {
            logger.info("'" + getComponentName() + "' with version '" + recommendedVersion + "' has been downloaded to '" + jarFile + "'.");
            return jarFile;
        }

        throw new RuntimeException("Could not download latest agent version.");
    }

    protected String tryGetRecommendedAgentFromServer(String jarsFolder, UpgradeResponse upgradeResponse) throws IOException {

        AgentInfo agentInfo = upgradeResponse.getAgent();
        if (agentInfo == null) {
            logger.error("Unable to get recommended agent from server. " +
                    "Recommended agent information was 'null'.");
            return null;
        }
        String version = agentInfo.getVersion();
        String url = agentInfo.getUrl();
        String zipFilePath = getAgentsTargetZipFilePath(jarsFolder);

        // try to download the agent to the target zip file.
        logger.info("Trying to upgrade to version '" + version + "' from url '" + url + "'.");
        if (!upgradeProxy.downloadAgent(url, zipFilePath)) {
            logger.warning("Failed to download agent with version '" + version + "'.");
        } else {
            logger.info("Download completed successfully.");
            rearrangeJarsFolder(jarsFolder);

            // try to unzip the downloaded agent.
            ArchiveUtils archiveUtils = new ArchiveUtils(logger);
            archiveUtils.unzip(zipFilePath, jarsFolder);
            tryDeleteDownloadedZip(zipFilePath);

            File newAgent = getDownloadedAgent(jarsFolder, version);
            if (newAgent.isFile())
                return newAgent.getAbsolutePath();

            logger.warning("Failed to unzip agent with version '" + version + "'.");
        }
        return null;
    }

    private String tryGetRecommendedAgentFromFolder(String jarsFolder, String recommendedVersion) {
        String jarNameToFind = getComponentNameDash() + recommendedVersion + ".jar";
        File recommendedAgent = FileAndFolderUtils.findFileInFolder(jarsFolder, jarNameToFind);

        if (recommendedAgent == null) {
            logger.info("Didn't find the recommended agent '" + jarNameToFind
                    + "' in folder '" + jarsFolder + "'.");
            return null;
        }
        if (!recommendedAgent.isFile()) {
            logger.warning("Didn't find the recommended agent '"
                    + recommendedAgent.getAbsolutePath() + "' in file system or it is not a valid file.");
            return null;
        }

        return recommendedAgent.getAbsolutePath();
    }

    private void tryDeleteDownloadedZip(String zipFilePath) {
        File zipFileToDelete = new File(zipFilePath);
        tryDeleteFile(zipFileToDelete, "agent zip file");
    }

    private String getAgentsTargetZipFilePath(String jarsFolder) {
        String fileName = getFileToDownloadName() + ".zip";
        return PathUtils.join(jarsFolder, fileName);
    }

    private File getDownloadedAgent(String jarsFolder, String version) {
        Version recommendedVersion = new Version(version);
        String newAgentName = getComponentNameDash() + recommendedVersion.get() + ".jar";
        String pathToNewAgent = PathUtils.join(jarsFolder, newAgentName);
        return new File(pathToNewAgent);
    }

    private void tryDeleteFile(File oldAgent, String descriptor) {
        try {
            if (oldAgent.delete()) {
                logger.info("Successfully deleted " + descriptor + ": '" + oldAgent.getAbsolutePath() + "'.");
                return;
            }
            if (oldAgent.exists()) {
                logger.warning("Failed to delete " + descriptor + ": '" + oldAgent.getAbsolutePath() + "'.");
            }

        } catch (Exception e) {
            logger.error("Failed to delete " + descriptor + ": '" + oldAgent.getAbsolutePath() + "'. Error: ", e);
        }
    }

    abstract protected ComponentName getComponentNameEnum();

    abstract String getFileToDownloadName();

    protected String getComponentName() {
        return getComponentNameEnum().toString();
    }

    private String getComponentNameDash() {
        return getComponentName() + "-";
    }
}