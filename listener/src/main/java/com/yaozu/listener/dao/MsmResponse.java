package com.yaozu.listener.dao;

/**
 * Created by jieyaozu on 2016/4/7.
 */
public class MsmResponse {
    public SendResponse alibaba_aliqin_fc_sms_num_send_response;

    public SendResponse getAlibaba_aliqin_fc_sms_num_send_response() {
        return alibaba_aliqin_fc_sms_num_send_response;
    }

    public void setAlibaba_aliqin_fc_sms_num_send_response(SendResponse alibaba_aliqin_fc_sms_num_send_response) {
        this.alibaba_aliqin_fc_sms_num_send_response = alibaba_aliqin_fc_sms_num_send_response;
    }

    public class SendResponse{
        public MsmResult result;

        public MsmResult getResult() {
            return result;
        }

        public void setResult(MsmResult result) {
            this.result = result;
        }

        public class MsmResult{
            public String err_code;
            public String model;
            public String success;
            public String msg;

            public String getErr_code() {
                return err_code;
            }

            public void setErr_code(String err_code) {
                this.err_code = err_code;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getSuccess() {
                return success;
            }

            public void setSuccess(String success) {
                this.success = success;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }
        }
    }
}
