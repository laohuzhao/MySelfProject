package test3;

public class ContextPlayer {
   private static player player;
   private static ContextPlayer contextPlayer;
   static {
       contextPlayer=new ContextPlayer();
   }
   public player doplay(String file){
       if ("a".equals(file)){
           player=new Aplayer();
       }else
       if ("b".equals(file)){
           player=new Bplayer();
       }else
       if ("map4".equals(file)){
           player=new map4player();
       }else {
           System.out.println("no"+file);
       }
        return player;
   }

   public static ContextPlayer getInstance(){
       return contextPlayer;
   }

    public static void main(String[] args) {
        ContextPlayer.getInstance().doplay("map4").doPlay("map4");
    }
}
