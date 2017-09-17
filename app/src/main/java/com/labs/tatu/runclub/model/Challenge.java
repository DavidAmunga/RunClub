package com.labs.tatu.runclub.model;

/**
 * Created by amush on 09-Sep-17.
 */

public class Challenge {
    private String challengeName,challengeLoc,challenger,challengeImage,challengeDesc;

    public Challenge(String challengeName, String challengeLoc, String challenger, String challengeImage, String challengeDesc) {
        this.challengeName = challengeName;
        this.challengeLoc = challengeLoc;
        this.challenger = challenger;
        this.challengeImage = challengeImage;
        this.challengeDesc = challengeDesc;
    }

    public Challenge()
    {

    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeLoc() {
        return challengeLoc;
    }

    public void setChallengeLoc(String challengeLoc) {
        this.challengeLoc = challengeLoc;
    }

    public String getChallenger() {
        return challenger;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public String getChallengeImage() {
        return challengeImage;
    }

    public void setChallengeImage(String challengeImage) {
        this.challengeImage = challengeImage;
    }

    public String getChallengeDesc() {
        return challengeDesc;
    }

    public void setChallengeDesc(String challengeDesc) {
        this.challengeDesc = challengeDesc;
    }
}
