package com.xck.leetcode;

public class LIS {

    static int LEN = 100005;
    int[] a = new int[LEN] ,b= new int[LEN];
    int[] loc = new int[LEN];
    int n;

    void calLoc()
    {
        int i;

        for(i = 1; i <= n; i++)
            loc[b[i]] = i;
    }

    int LIS() {
        int i, k, l, r, mid;

        a[1] = b[1];
        k = 1;
        for(i = 2; i <= n; i++)
        {
            if(a[k] < b[i]) a[++k] = b[i];
            else {
                l = 1; r = k;
                while(l <= r)
                {
                    mid = ( l + r ) / 2;
                    if(a[mid] < b[i])
                        l = mid + 1;
                    else
                        r = mid - 1;

                }
                a[l] = b[i];
            }
        }
        return k;
    }

    public int lis(String s1, String s2) {
        int i, steps, ln, rn;
        int max;
        int moves;
        String l;
        String r;
        int n1=s1.length();
        int n2 = s2.length();
        if (n1 < n2) {
            l = s1;
            r = s2;
            ln = n1;
            rn = n2;
        } else {
            l = s2;
            r = s1;
            ln = n2;
            rn = n1;
        }

        max = 0;
        for (steps = 0; steps < ln + rn - 1; ++steps) {
            moves = 0;
//检测每次移动后子串重合的情况, 这里可以优化的， 我这里只是把意思表示出来， 忽略一些细节:-)
            for (i = steps; i < steps + ln; ++i) {
                if (i < ln - 1) continue;
                if (i > (ln + rn - 1) - 1) {
                    max = moves > max ? moves : max;
                    break;
                }

                if (l.charAt(i - steps) == r.charAt(i - ln + 1)) {
                    ++moves;
                    max = moves > max ? moves : max;
                } else {
                    max = moves > max ? moves : max;
                    moves = 0;
                }
            }
        }

        return max;
    }

    public static void main(String[] args) {
        String s1 = "【顺丰快递】你好啊，亲爱的张三先生";
        String s2 = "【圆通快递】你好啊，亲爱的徐女士";
        System.out.println(new LIS().LIS());
    }
}
