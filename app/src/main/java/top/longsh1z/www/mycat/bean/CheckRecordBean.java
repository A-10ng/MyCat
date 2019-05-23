package top.longsh1z.www.mycat.bean;

import java.util.List;

public class CheckRecordBean {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class ContentBean{
        private String recordContent;
        private String recordTime;

        public String getRecordContent() {
            return recordContent;
        }

        public void setRecordContent(String recordContent) {
            this.recordContent = recordContent;
        }

        public String getRecordTime() {
            return recordTime;
        }

        public void setRecordTime(String recordTime) {
            this.recordTime = recordTime;
        }
    }
}
