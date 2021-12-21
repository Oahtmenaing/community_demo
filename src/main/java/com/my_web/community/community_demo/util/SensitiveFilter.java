package com.my_web.community.community_demo.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
                         BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));)
        {
            String keyword;
            while ((keyword=bufferReader.readLine()) != null){
                this.getkeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("获取敏感词文件失败，" + e.getMessage());
        }

    }

    private void getkeyword(String keyword) {
        TrieNode currentNode = rootNode;
        for (int i=0; i<keyword.length(); i ++){
            Character key = keyword.charAt(i);
            TrieNode sub_node = currentNode.getSub_node(key);
            if (sub_node == null){
                currentNode.addSub_node(key, new TrieNode());
            }
            currentNode = currentNode.getSub_node(key);
            if (i == keyword.length() - 1) {
                currentNode.setTrieEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)){
            return null;
        }
        TrieNode currentNode = rootNode;
        int begin = 0;
        int end = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (end < text.length()) {
            char c = text.charAt(end);
            if (isSymbol(c)){
                // 如果指针1为根节点，直接移动指针1；
                if (currentNode == rootNode){
                    stringBuilder.append(c);
                    begin ++;
                }
                end ++;
                continue;
            }
            // 如果不为符号
            currentNode = currentNode.getSub_node(c);
            if (currentNode == null){
                stringBuilder.append(text.charAt(begin));
                end = ++begin;
                currentNode = rootNode;
            } else if (currentNode.isTrieEnd()) {
                stringBuilder.append(REPLACEMENT);
                // 发现敏感词
                begin = ++ end;
                currentNode = rootNode;
            } else {
                end ++;
            }
        }
        // 最后一批词，未完成完整树
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
    }

    private boolean isSymbol(char c){
        // c < 0x2E80 || c > 0x9fff为东亚字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9fff);
    }

    private class TrieNode {
        private boolean isTrieEnd = false;

        public boolean isTrieEnd() {
            return isTrieEnd;
        }

        private void setTrieEnd(boolean bool) {
            isTrieEnd = bool;
        }

        private Map<Character, TrieNode> sub_node = new HashMap<>();

        public void addSub_node(Character c, TrieNode node) {
            sub_node.put(c, node);
        }

        public TrieNode getSub_node(Character c) {
            return sub_node.get(c);
        }
    }
}
