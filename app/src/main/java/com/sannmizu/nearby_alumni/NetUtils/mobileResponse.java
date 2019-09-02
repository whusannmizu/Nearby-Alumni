package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.POST;

public class mobileResponse extends MyResponse<mobileResponse.mobileData>{
    private static interface mobileService{
        //@POST("/2013-12-26/Accounts")
        //Call<mobileResponse>mobile()
    }
    public static class mobileData{
        @SerializedName("statusCode")
        private String statusCode;
        @SerializedName("templateSMS")
        private TemplateSMS templateSMS;
        public static class TemplateSMS{
        @SerializedName("dateCreated")
            private String dateCreated;
        @SerializedName("smsMessageSid")
            private String smsMessageSid;

            public void setDateCreated(String dateCreated) {
                this.dateCreated = dateCreated;
            }

            public String getDateCreated() {
                return dateCreated;
            }

            public void setSmsMessageSid(String smsMessageSid) {
                this.smsMessageSid = smsMessageSid;
            }

            public String getSmsMessageSid() {
                return smsMessageSid;
            }
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setTemplateSMS(TemplateSMS templateSMS) {
            this.templateSMS = templateSMS;
        }

        public TemplateSMS getTemplateSMS() {
            return templateSMS;
        }
    }
}
