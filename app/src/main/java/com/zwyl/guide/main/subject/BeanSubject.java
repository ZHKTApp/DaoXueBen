package com.zwyl.guide.main.subject;

import java.util.List;

public class BeanSubject {


    /**
     * answerFileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/null
     * exercisesId : timu001
     * exercisesTitle : 下列说法中正确的是
     * studentAnswerFileUri : null
     * optionId : null
     * exerciseAnswer : danxuan001
     * analysisFileUri : http://art.test.internet.zhiwangyilian.com/images/artImg/3464645
     * exerciseAnalysis : 解析这是解析
     * exerciseOptionList : [{"optionId":"danxuan001","optionNo":"A","rankNo":1,"isRight":true,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"timu001","optionContent":"北京"},{"optionId":"danxuan002","optionNo":"B","rankNo":2,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"timu001","optionContent":"上海"},{"optionId":"danxuan003","optionNo":"C","rankNo":3,"isRight":false,"createTime":"2017-06-03 15:59:59","createUser":"001","lastUpdateTime":"2017-06-03 15:59:59","lastUpdateUser":"001","isDeleted":false,"exerciseId":"timu001","optionContent":"广州"}]
     * rankNo : 1
     * exerciseTypeCode : 30101
     */

    public String answerFileUri;
    public boolean isSeeAnswer;
    public String exercisesId;
    public String exercisesTitle;
    public String studentAnswerFileUri;
    public String optionId;
    public String exerciseAnswer;
    public String analysisFileUri;
    public String exerciseAnalysis;
    public int rankNo;
    public int exerciseTypeCode;
    public List<ExerciseOptionListBean> exerciseOptionList;
    public String studentOptionId;
    public String studentAnswerOptionId;
    public static class ExerciseOptionListBean {
        /**
         * optionId : danxuan001
         * optionNo : A
         * rankNo : 1
         * isRight : true
         * createTime : 2017-06-03 15:59:59
         * createUser : 001
         * lastUpdateTime : 2017-06-03 15:59:59
         * lastUpdateUser : 001
         * isDeleted : false
         * exerciseId : timu001
         * optionContent : 北京
         */

        public String optionId;
        public String optionNo;
        public int rankNo;
        public boolean isRight;
        public String createTime;
        public String createUser;
        public String lastUpdateTime;
        public String lastUpdateUser;
        public boolean isDeleted;
        public String exerciseId;
        public String optionContent;
        public boolean ischeck;
    }
}
