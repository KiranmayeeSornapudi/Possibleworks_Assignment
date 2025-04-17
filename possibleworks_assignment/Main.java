import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("input2.json")));
        JSONObject json = new JSONObject(content);

        // Extract keys separately
        JSONObject keysObject = json.getJSONObject("keys");
        int k = keysObject.getInt("k");

        List<Point> points = new ArrayList<>();

        // Extract all other entries as points
        for (String key : json.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt(key);
            JSONObject obj = json.getJSONObject(key);
            int base = Integer.parseInt(obj.getString("base"));
            String valueStr = obj.getString("value");
            BigInteger y = new BigInteger(valueStr, base);
            points.add(new Point(BigInteger.valueOf(x), y));
        }

        // Sort by x value to ensure consistency
        points.sort(Comparator.comparing(p -> p.x));

        // Use only first k points
        List<Point> selected = points.subList(0, k);
        BigInteger result = lagrangeInterpolationAtZero(selected);
        System.out.println("Secret (c) = " + result);
    }

    static BigInteger lagrangeInterpolationAtZero(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
