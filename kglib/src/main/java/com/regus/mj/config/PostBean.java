package com.regus.mj.config;

import java.io.Serializable;

public class PostBean implements Serializable {


    /**
     * ClientSource : 0
     * Param : {"AppKey":"U20210624154154372928","ChannelId":"","Mac":""}
     * Date : 1627384574670
     * Token :
     * Sign : b08457aa7a5c9bccdc3dbc7020912ba3
     * PartnerKey : b82cc1515cd64869beefe697cce16aad
     */

    private Integer ClientSource;
    private ParamBean Param;
    private String Date;
    private String Token;
    private String Sign;
    private String PartnerKey;

    public Integer getClientSource() {
        return ClientSource;
    }

    public void setClientSource(Integer clientSource) {
        ClientSource = clientSource;
    }

    public ParamBean getParam() {
        return Param;
    }

    public void setParam(ParamBean param) {
        Param = param;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getPartnerKey() {
        return PartnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        PartnerKey = partnerKey;
    }

    public static class ParamBean {
        /**
         * AppKey : U20210624154154372928
         * ChannelId :
         * Mac :
         */

        private String AppKey;
        private String ChannelId;
        private String Mac;

        public String getAppKey() {
            return AppKey;
        }

        public void setAppKey(String appKey) {
            AppKey = appKey;
        }

        public String getChannelId() {
            return ChannelId;
        }

        public void setChannelId(String channelId) {
            ChannelId = channelId;
        }

        public String getMac() {
            return Mac;
        }

        public void setMac(String mac) {
            Mac = mac;
        }
    }

}
