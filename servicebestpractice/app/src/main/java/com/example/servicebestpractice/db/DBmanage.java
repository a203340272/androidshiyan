package com.example.servicebestpractice.db;

import org.litepal.LitePal;

import java.util.List;

public class DBmanage {


    //增加歌曲信息
    public static void addQuku(String author, String name, String songURL) {
        Quku quku = new Quku();
        quku.setAuthor(author);
        quku.setSongname(name);
        quku.setSongURL(songURL);
        quku.save();
    }
    //得到所有的曲库信息
    public List<Quku> getAllQuku() {
        List<Quku> qukuList=LitePal.findAll(Quku.class);
        return qukuList;
    }

    //判断曲库里是否已经有这首歌了
    public boolean noExistQuku(String name,String path){
        List<Quku> qukuList=LitePal.where("songname = ? and songURL = ?", name,path).find(Quku.class);
        if (qukuList.isEmpty()){
            return true;
        }
        else
            return false;
    }

    //增加歌曲信息,名字和路径
    public static void addSongInfo(long songId,long albumId,String songName,String songAlbum,String songPath,String singerName,int songLength) {
        SongInfo songinfo = new SongInfo();
        songinfo.setSongId(songId);
        songinfo.setAlbumId(albumId);
        songinfo.setSongName(songName);
        songinfo.setSongAlbum(songAlbum);
        songinfo.setSongPath(songPath);
        songinfo.setSingerName(singerName);
        songinfo.setSongLength(songLength);
        songinfo.save();
    }
    //得到所有的songinfo
    public List<SongInfo> getAllSongInfo() {
        List<SongInfo> songinfolist=LitePal.findAll(SongInfo.class);
        return songinfolist;
    }
    //删除某一songinfo
    public static void deleteSongInfo(int id){
        LitePal.delete(SongInfo.class, id);
    }

    //删除所有songinfo
    public void deleteAllSongInfo(){
        LitePal.deleteAll(SongInfo.class,null);
    }
    //删除所有quku
    public void deleteAllQuku(){
        LitePal.deleteAll(Quku.class,null);
    }

    //是否已经存在该SongInfo对象了
    public boolean noExistSongInfo(String name){
        List<SongInfo> songInfoList=LitePal.where("songName = ?", name).find(SongInfo.class);
        if (songInfoList.isEmpty()){
            return true;
        }
        else
            return false;
    }
//    //根据歌曲名（SongInfo）得到歌手名（Quku）
//    public String getSingerNameBySongName(String songName){
//        String name;
//        List<Quku> qukuList=LitePal.where("songname like ?", songName+"%").find(Quku.class);
//        if(qukuList.size()==0) {
//            name="";
//        }else {
//            Quku quku = qukuList.get(0);
//            name=quku.getAuthor();
//        }
//        return name;
//    }

}

