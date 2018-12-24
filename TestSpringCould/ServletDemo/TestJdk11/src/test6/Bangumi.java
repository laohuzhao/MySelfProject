package test6;

import java.util.ArrayList;
import java.util.List;

public class Bangumi implements BangumiSubject{
    private List<UserObserver> list;
    private String  anime;
    public Bangumi(String anime) {
        this.anime = anime;
        list = new ArrayList<UserObserver>();
    }

    @Override
    public void toThem(UserObserver user) {
        System.out.println("用户"+user.getName()+"订阅了"+anime+"!");
        list.add(user);
    }

    @Override
    public void callOff(UserObserver user) {
        if(!list.isEmpty())
            System.out.println("用户"+user.getName()+"取消订阅"+anime+"!");
        list.remove(user);
    }

    @Override
    public void notifyUser() {
        System.out.println(anime+"更新了！开始通知订阅该番剧的用户！");
        list.forEach(user->
                user.update(anime)
        );
    }
}
