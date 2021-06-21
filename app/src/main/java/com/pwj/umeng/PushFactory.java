package com.pwj.umeng;

import java.io.Serializable;

public class PushFactory implements Serializable{


    /**
     * display_type : notification
     * extra : {"type_class":"1","from":"17600482472","table_id":"311","table_name":"require_1_purchase"}
     * body : {"after_open":"go_app","ticker":"Android customizedcast ticker","title":"新需求提醒","play_sound":"true","play_lights":"true","play_vibrate":"true","text":"雷行机械抛丸17879723668发布了求购需求"}
     * msg_id : uant8f5162385183054801
     */

    private String display_type;
    private ExtraBean extra;
    private BodyBean body;
    private String msg_id;


    public String getDisplay_type() {
        return display_type;
    }

    public void setDisplay_type(String display_type) {
        this.display_type = display_type;
    }

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public static class ExtraBean {
        /**
         * type_class : 1
         * from : 17600482472
         * table_id : 311
         * table_name : require_1_purchase
         */

        private String type_class;
        private String from;
        private String table_id;
        private String table_name;

        public String getType_class() {
            return type_class;
        }

        public void setType_class(String type_class) {
            this.type_class = type_class;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTable_id() {
            return table_id;
        }

        public void setTable_id(String table_id) {
            this.table_id = table_id;
        }

        public String getTable_name() {
            return table_name;
        }

        public void setTable_name(String table_name) {
            this.table_name = table_name;
        }
    }

    public static class BodyBean {
        /**
         * after_open : go_app
         * ticker : Android customizedcast ticker
         * title : 新需求提醒
         * play_sound : true
         * play_lights : true
         * play_vibrate : true
         * text : 雷行机械抛丸17879723668发布了求购需求
         */

        private String after_open;
        private String ticker;
        private String title;
        private String play_sound;
        private String play_lights;
        private String play_vibrate;
        private String text;

        public String getAfter_open() {
            return after_open;
        }

        public void setAfter_open(String after_open) {
            this.after_open = after_open;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPlay_sound() {
            return play_sound;
        }

        public void setPlay_sound(String play_sound) {
            this.play_sound = play_sound;
        }

        public String getPlay_lights() {
            return play_lights;
        }

        public void setPlay_lights(String play_lights) {
            this.play_lights = play_lights;
        }

        public String getPlay_vibrate() {
            return play_vibrate;
        }

        public void setPlay_vibrate(String play_vibrate) {
            this.play_vibrate = play_vibrate;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
