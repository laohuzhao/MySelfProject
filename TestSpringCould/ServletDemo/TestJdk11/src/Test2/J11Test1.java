package Test2;

public interface J11Test1 {
    String getOld();
    default String getNew(){
        return "I am is java11 new Interface";
    }
}
