package test6;

public class test {
    public static void main(String[] args) {
        String name1 ="张三";
        String name2 ="xuwujing";
        String	bingguo = "冰菓";
        String	fate = "fate/zero";
        BangumiSubject bs1 = new Bangumi(bingguo);
        BangumiSubject bs2 = new Bangumi(fate);

        UserObserver uo1 = new User(name1);
        UserObserver uo2 = new User(name2);

        //进行订阅
        bs1.toThem(uo1);
        bs1.toThem(uo2);
        bs2.toThem(uo1);
        bs2.toThem(uo2);
        //进行通知
        bs1.notifyUser();
        bs2.notifyUser();

        //取消订阅
        bs1.callOff(uo1);
        bs2.callOff(uo2);
        //进行通知
        bs1.notifyUser();
        bs2.notifyUser();
    }
}
