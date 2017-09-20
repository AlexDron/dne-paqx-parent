package com.dell.cpsd.paqx.dne.service.model;

public class ValidateProtectionDomainResponse extends TaskResponse {

    private String protectionDomains;
//    private String errorMessage;
//    private List<String> warningMessages;
//    private List<String> recommendedMessages;

    public ValidateProtectionDomainResponse() {}

    public ValidateProtectionDomainResponse(String protectionDomains) {
        this.protectionDomains = protectionDomains;
//        this.errorMessage = errorMessage;
//        this.warningMessages = warningMessages;
//        this.recommendedMessages = recommendedMessages;
    }

    public String getProtectionDomains() {
        return protectionDomains;
    }

    public void setProtectionDomains(String protectionDomains) {
        this.protectionDomains = protectionDomains;
    }

//    public String getErrorMessage() {
//        return errorMessage;
//    }
//
//    public void setErrorMessage(String errorMessage) {
//        this.errorMessage = errorMessage;
//    }
//
//    public List<String> getWarningMessages() {
//        return warningMessages;
//    }
//
//    public void setWarningMessages(List<String> warningMessages) {
//        this.warningMessages = warningMessages;
//    }
//
//    public List<String> getRecommendedMessages() {
//        return recommendedMessages;
//    }
//
//    public void setRecommendedMessages(List<String> recommendedMessages) {
//        this.recommendedMessages = recommendedMessages;
//    }

    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("ProtectionDomains{");
        builder.append("protectionDomains=").append(this.protectionDomains);
//        builder.append("errorMessage=").append(this.errorMessage);
//        builder.append("warningMessages=").append(this.warningMessages);
//        builder.append("recommendedMessages=").append(this.recommendedMessages);
        builder.append("}");

        return builder.toString();

    }

}
