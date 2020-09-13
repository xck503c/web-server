import com.github.mgunlogson.cuckoofilter4j.CuckooFilter;
import com.google.common.hash.Funnels;
import net.cinnom.nanocuckoo.NanoCuckooFilter;

import java.nio.charset.Charset;

public class TestCuckooFilter {

    public static void main(String[] args) throws Exception {
        test2();
    }

    public static void test1() throws Exception {
        // create
        CuckooFilter<String> filter = new CuckooFilter.Builder<String>(
                Funnels.stringFunnel(Charset.forName("utf-8")), 100000000).build();
        // insert
        int j = 0;
        for (long i = 15700000000L; i <= 15729999999L; i++) {
            ++j;
            filter.put(i + "");
            if (j % 100000 == 0) {
                System.out.println(i);
                Thread.sleep(10);
            }
        }
        j = 0;

        int count = 0;
        for (long i = 15700000000L; i <= 15749999999L; i++) {
            ++j;
            if (filter.mightContain(i + "")) {
                ++count;
            }
            if (j % 100000 == 0) {
                System.out.println(count);
                Thread.sleep(10);
            }
        }
        System.out.println(count);
    }

    public static void test2() throws Exception {
        // create
        final NanoCuckooFilter filter = new NanoCuckooFilter.Builder(50000000)
                .withCountingEnabled(true) // Enable counting
                .build();
        // insert
        int j = 0;
        for (long i = 15700000000L; i <= 15729999999L; i++) {
            ++j;
            filter.insert(i + "");
            if (j % 100000 == 0) {
                System.out.println(i);
                Thread.sleep(10);
            }
        }
        j = 0;

        int count = 0;
        for (long i = 15700000000L; i <= 15749999999L; i++) {
            ++j;
            if (filter.contains(i + "")) {
                ++count;
            }
            if (j % 100000 == 0) {
                System.out.println(count);
                Thread.sleep(10);
            }
        }
        System.out.println(count);
    }
}
