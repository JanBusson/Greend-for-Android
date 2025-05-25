package com.example.greendr;

/*########################################################
CurrUser ist ein Singleton Object was den verschiedenen Activities/Views ermöglicht zu schauen
 um welche Nutzer es sich handelt. (Z.b. um zu beantworten wem beim Matchen die Profile zugeordnet werden)
########################################################*/
public class CurrUser {

    int userID;
    private CurrUser(){

    }

    //Lesen um welchen User es sich handelt (mach tSinn sobald ein User eingelogged ist, z.B. bei LikedUsers anzeigen)
    public int getUser(){
        return userID;
    }

    //Festlegen um welchen User es sich handelt (wird beim erfolgreichen Login eingeführt)
    public void setUser(int newUser){
        this.userID=newUser;
    }
}
