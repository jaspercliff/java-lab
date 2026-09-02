package com;

import com.jasper.io.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileUtilTest {

    @Test
    public void test(){
        File file = new File("/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/testUtil.xlsx");
        int fileLines = FileUtil.getFileLines(file);
        System.out.println("fileLines = " + fileLines);
        File file1 = new File("/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/test.txt");
        int fileLines1 = FileUtil.getFileLines(file1);
        System.out.println("fileLines1 = " + fileLines1);
    }

    @Test
    public void testCopyFile(){
        String srcPath = "/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/test.txt";
        String desPath = "/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/files";
        FileUtil.copyFile(srcPath, desPath);
    }

    @Test
    public void testCopyFileStream(){
        String srcPath = "/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/test.txt";
        String desPath = "/Users/jasper/IdeaProjects/person/nebulaAdmin/nebulaFramework/common/src/main/test/com/jasper/files";
        FileUtil.copyFileStream(srcPath, desPath);
    }
}
