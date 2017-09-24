package com.labs.tatu.runclub.model;

/**
 * Created by amush on 09-Sep-17.
 */

public class Challenge {
    private String challengeName,challenger,challengeImage;

    public Challenge(String challengeName, String challenger, String challengeImage) {
        this.challengeName = challengeName;

        this.challenger = challenger;
        this.challengeImage = challengeImage;

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


}
