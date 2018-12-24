package test5;

public class JWzw {


    public void sss( Integer [] a){
        int num = 0;
        int upnum = 0;
        for (int i=a.length;i>=0;i--){

            //1.第一个和第二个对比，如果发现第一个比第二个小，那么位置不动
            for (int j=0;j<i-1;j++){
                num++;
                if (a[i-2]>=a[i-1]){
                    upnum++;
                    int b=a[i];
                    a[i-1]=a[i-2];
                    a[i-2]=b;
                }else {
                    //位置不动
                    break;
                }
            }
            //2.第二个和第三个对比，如果说第二个比第三个小，位置不动，如果第二个比第三个大，那么位置互换，然后执行第一步

            //4.第三个和第四个对比，如果三个比第四个小，位置不动，如果比第四个大，位置互换，执行第2步
        }
        System.out.println("自定义排序移动次数"+upnum+"循环次数"+num);
        for (int i=0;i<a.length;i++){
            System.out.println(a[i]);
        }

    }


    //插入排序
    public void insertArray(Integer[] in) {
        int tem = 0;
        int num = 0;
        int upnum = 0;
        for (int i = 0; i < in.length; i++) {
            for (int j = i - 1; j >= 0; j--) {
                num++;
                if (in[j + 1] < in[j]) {
                    tem = in[j + 1];
                    in[j + 1] = in[j];
                    in[j] = tem;
                    upnum++;
                } else {
                    break;
                }
            }
        }
        for (int i = 0; i < in.length; i++) {
            System.out.print(in[i]);
            if (i < in.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
        System.out.println("插入排序循环次数:" + num);
        System.out.println("移动次数：" + upnum);
    }
    //选择排序
    public void chooseArray(Integer[] in) {
        int tem = 0;
        int num = 0;
        int upnum = 0;
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in.length - 1; j++) {
                num++;
                if (in[j + 1] < in[j]) {
                    tem = in[j + 1];
                    in[j + 1] = in[j];
                    in[j] = tem;
                    upnum++;
                }
            }
        }
        for (int i = 0; i < in.length; i++) {
            System.out.print(in[i]);
            if (i < in.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
        System.out.println("选择排序循环次数:" + num);
        System.out.println("移动次数：" + upnum);
    }
    //冒泡排序
    public void efferArray(Integer[] in) {
        int tem = 0;
        int num = 0;
        int upnum = 0;
        for (int i = 0; i < in.length; i++) {
            for (int j = i; j < in.length - 1; j++) {
                num++;
                if (in[j + 1] < in[i]) {
                    tem = in[j + 1];
                    in[j + 1] = in[i];
                    in[i] = tem;
                    upnum++;
                }
            }
        }
        for (int i = 0; i < in.length; i++) {
            System.out.print(in[i]);
            if (i < in.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println();
        System.out.println("冒泡排序循环次数:" + num);
        System.out.println("移动次数：" + upnum);
    }
    //打印乘法口诀


    public void printMulti() {
        for (int j = 1; j < 10; j++) {
            for (int i = 1; i <= j; i++) {
                System.out.print(i + " * " + j + " = " + j * i + "\t");
            }
            System.out.print("\t\n");
        }
    }
    //打印N * 1 + N * 2 + N * 3 =num的所有组合


    public void printNumAssemble(int num) {
        for (int i = 0; i < num + 1; i++) {
            for (int j = 0; j < num / 2 + 1; j++) {
                for (int in = 0; in < num / 3 + 1; in++) {
                    if (i * 1 + j * 2 + in * 3 == num) {
                        System.out.println("小马" + i + ",\t中马" + j + ",\t大马" + in);
                    }
                }
            }
        }
    }

    /**
     * @param args
     */


    public static void main(String[] args) {
        JWzw jwzw = new JWzw();
       // int num = 3;
       // jwzw.printMulti(); //打印乘法口诀
        //jwzw.printNumAssemble(100); //打印N * 1 + N * 2 + N * 3 =num的所有组合
        Integer in[] = {
                8, 89, 5, 84, 3, 45, 12, 33, 77, 98, 456, 878, 654, 213, 897
        };
        jwzw.efferArray(in); //冒泡排序
        Integer in1[] = {
                8, 89, 5, 84, 3, 45, 12, 33, 77, 98, 456, 878, 654, 213, 897
        };
        jwzw.insertArray(in1); //插入排序
        Integer in2[] = {
                8, 89, 5, 84, 3, 45, 12, 33, 77, 98, 456, 878, 654, 213, 897
        };
        jwzw.chooseArray(in2); //选择排序
        Integer in3[] = {
                8, 89, 5, 84, 3, 45, 12, 33, 77, 98, 456, 878, 654, 213, 897
        };
        jwzw.sss(in3);
        //int i = num++;
        //System.out.println(i);
        //System.out.println(1000 >> 2);
    }

}
