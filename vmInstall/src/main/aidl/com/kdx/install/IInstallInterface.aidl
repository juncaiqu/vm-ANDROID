package com.kdx.install;

interface IInstallInterface {
    boolean install(String apkPath);
    boolean uninstall(String apkPath);
    String execCmd(String cmd);
}
