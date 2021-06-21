package com.pwj.bean;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;


public class WordModel implements SortedListAdapter.ViewModel {

    private  long mId;
    private final int mRank;    //这个是哪一行的id
    private final String mWord; //项目名称
    private String time;
    private String link;
    private String contact;     //联系人名字
    private String phone;       //联系人电话
    private String issue_date; //发布日期
    private String valid_date; //发布日期
    private String location;    //位置
    private String identity;        //身份
    private String remarks;     //应聘补充
    private String company;     //公司名称用mWord代替
    private String count;  //该公司的历史招标数量
    private String content;  //该公司招标的内容，便于检索
    public WordModel(long id, int rank, String word) {
        mId = id;
        mRank = rank; //为id的哪一条
        mWord = word; //项目名称

    }
    //招标公告的表
//    public WordModel(long mId, int mRank, String mWord, String time, String link) {
//        this.mId = mId;
//        this.mRank = mRank;
//        this.mWord = mWord;
//        this.time = time;
//        this.link = link;
//    }

    //招标公告搜索时候的列表  用mWord代替company的名字
    public WordModel(long mId, int mRank, String mWord, String count, String content) {
        this.mId = mId;
        this.mRank = mRank;
        this.mWord = mWord;//公司名称
        this.count = count;
        this.content = content; //为了检索内容包含的关键词
    }

    //应聘表

                                //mWord第一个就当联系人String contact,
    public WordModel(int mRank, String mWord,  String phone, String issue_date, String valid_date, String location, String identity, String remarks) {
        this.mRank = mRank;
        this.mWord = mWord;
        this.phone = phone;
        this.issue_date = issue_date;
        this.valid_date = valid_date;
        this.location = location;
        this.identity = identity;
        this.remarks = remarks;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getValid_date() {
        return valid_date;
    }

    public void setValid_date(String valid_date) {
        this.valid_date = valid_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public long getId() {
        return mId;
    }

    public int getRank() {
        return mRank;
    }

    public String getWord() {
        return mWord;
    }

    public String getContent() {
        return content;
    }

    @Override
    public <T> boolean isSameModelAs(@NonNull T item) {
        if (item instanceof WordModel) {
            final WordModel wordModel = (WordModel) item;
            return wordModel.mId == mId;
        }
        return false;
    }

    @Override
    public <T> boolean isContentTheSameAs(@NonNull T item) {
        if (item instanceof WordModel) {
            final WordModel other = (WordModel) item;
            if (mRank != other.mRank) {
                return false;
            }
            return mWord != null ? mWord.equals(other.mWord) : other.mWord == null;
        }
        return false;
    }

}
