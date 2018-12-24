package test6;

public class User implements UserObserver{
    private String name;
    public User(String name){
        this.name = name;
    }

    @Override
    public void update(String bangumi) {
        System.out.println(name+"订阅的番剧: " + bangumi+"更新啦！");
    }

    @Override
    public String getName() {
        return name;
    }
}
