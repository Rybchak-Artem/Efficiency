import java.util.Arrays;
import java.util.Random;
import java.util.function.ToIntFunction;

public class Efficiency {

    private static final int K = 6;
    private static final int RUNS = 3;
    private static final int[] SIZES = {10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};
    private static final Random RANDOM = new Random();

    public static int[] generateArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = RANDOM.nextInt(20_001) - 10_000;
        }
        return array;
    }

    public static int sixthBySorting(int[] array) {
        int[] copy = array.clone();
        Arrays.sort(copy);
        return copy[copy.length - K];
    }

    public static int sixthByTopArray(int[] array) {
        int[] top = new int[K];
        Arrays.fill(top, Integer.MIN_VALUE);

        for (int x : array) {
            if (x > top[K - 1]) {
                int j = K - 1;
                while (j > 0 && top[j - 1] < x) { 
                    top[j] = top[j - 1];
                    j--;
                }
                top[j] = x;
            }
        }
        return top[K - 1];
    }

    public static int sixthByMaxSearch(int[] array) {
        int[] indexes = new int[K];
        int[] values = new int[K];
        int result = 0;

        for (int k = 0; k < K; k++) {
            int maxIndex = 0;
            for (int i = 1; i < array.length; i++) {
                if (array[i] > array[maxIndex]) {
                    maxIndex = i;
                }
            }
            indexes[k] = maxIndex;
            values[k] = array[maxIndex];
            result = array[maxIndex];
            array[maxIndex] = Integer.MIN_VALUE;
        }

        for (int k = K - 1; k >= 0; k--) {
            array[indexes[k]] = values[k];
        }
        return result;
    }

    private static String cell(long[] runTimes) {
        long sum = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < runTimes.length; i++) {
            sum += runTimes[i];
            sb.append(i == 0 ? "" : " ").append(runTimes[i]);
        }
        return Math.round((double) sum / runTimes.length) + " (" + sb + ")";
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        int[] test = {5, 1, 9, 3, 7, 2, 8, 4, 6, 10};
        System.out.println("Перевірка: " + sixthBySorting(test) + " "
                + sixthByTopArray(test) + " " + sixthByMaxSearch(test) + " (очікується 5 5 5)\n");

        ToIntFunction<int[]>[] methods = new ToIntFunction[]{
                (ToIntFunction<int[]>) Efficiency::sixthBySorting,
                (ToIntFunction<int[]>) Efficiency::sixthByTopArray,
                (ToIntFunction<int[]>) Efficiency::sixthByMaxSearch
        };

        int[] warm = generateArray(100_000);
        for (int i = 0; i < 20; i++) {
            for (ToIntFunction<int[]> m : methods) {
                m.applyAsInt(warm);
            }
        }

        long[][][] times = new long[SIZES.length][methods.length][RUNS];

        for (int s = 0; s < SIZES.length; s++) {
            System.out.println("Розмір " + SIZES[s] + "...");
            for (int r = 0; r < RUNS; r++) {
                int[] data = generateArray(SIZES[s]);
                int expected = 0;

                for (int m = 0; m < methods.length; m++) {
                    long start = System.currentTimeMillis();
                    int result = methods[m].applyAsInt(data);
                    long end = System.currentTimeMillis();

                    times[s][m][r] = end - start;

                    if (m == 0) {
                        expected = result;
                    } else if (result != expected) {
                        System.out.println("  !!! Результати не збігаються: спосіб " + (m + 1)
                                + " дав " + result + ", очікувалось " + expected);
                    }
                }
            }
        }

        System.out.println("\nЧас роботи, мс: середнє (запуск1 запуск2 запуск3)\n");
        System.out.printf("%-14s | %-28s | %-28s | %-28s%n",
                "Розмір даних", "Спосіб 1 (Сортування)", "Спосіб 2 (Масив)", "Спосіб 3 (Максимуми)");
        System.out.println("-".repeat(110));
        for (int s = 0; s < SIZES.length; s++) {
            System.out.printf("%-14d | %-28s | %-28s | %-28s%n", SIZES[s],
                    cell(times[s][0]), cell(times[s][1]), cell(times[s][2]));
        }
    }
}
