package com.arraywork.imagewise.service;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

/**
 * Directory Listener
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/06/04
 */
public class DirectoryListener extends FileAlterationListenerAdaptor {

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("新建：" + directory.getPath());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除：" + directory.getPath());
    }

}