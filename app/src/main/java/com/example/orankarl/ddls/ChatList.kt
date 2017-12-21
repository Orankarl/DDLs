package com.example.orankarl.ddls

import java.util.*

/**
 * Created by orankarl on 2017/12/20.
 */
class ChatItem(var title:String, var latestSender:String, var latestMessage:String, var imagePath:String, var latestTime:Date)
class ChatList() {
    private var chatList: MutableList<ChatItem> = mutableListOf()

    fun loadChatList() {
        //load local data
        chatList.clear()
        this.chatList.add(ChatItem("数据库", "A", "Hello world!", "", Date()))
        this.chatList.add(ChatItem("人工智能", "B", "こんにちは～", "", Date()))
    }
    fun updateChatList() {
        //query newest data from server, update local data
    }
    fun size():Int {
        return chatList.size
    }
    fun get(position:Int):ChatItem {
        return chatList[position]
    }

}