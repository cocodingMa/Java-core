import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class RedPackage {

    //最小的红包0.01
    public static BigDecimal MIN_PACKAGE = BigDecimal.valueOf(0.01D);

    //最大红包是平均金额MONEY_TIME倍
    public static final long MONEY_TIME = 2L;

    //拆红包
    public static List<BigDecimal> splitRedPackage(BigDecimal money, int count){
        List<BigDecimal> list = new LinkedList<BigDecimal>();

        BigDecimal max_package = divideMoney(money, count).multiply(BigDecimal.valueOf(MONEY_TIME));
        //随机获取红包
        for (int i = 0; i < count; i++){
            BigDecimal perRedPackage = perRedPackage(money, count-i, max_package).setScale(2,BigDecimal.ROUND_HALF_UP);
            list.add(perRedPackage);
            money = money.subtract(perRedPackage);
        }

        return list;
    };

    //生成具体的红包
    public static BigDecimal perRedPackage(BigDecimal remainMoney, int remainSize, BigDecimal max) {
        if (remainSize == 1){
            return remainMoney;
        }
        BigDecimal maxPer = divideMoney(remainMoney, remainSize).multiply(BigDecimal.valueOf(MONEY_TIME));
        BigDecimal redPacket = (BigDecimal.valueOf(Math.random()).multiply(maxPer.subtract(MIN_PACKAGE)));

        //如果剩下的红包过大或者过小，此红包重新生成
        if(checkRemainMoney(remainMoney.subtract(redPacket), remainSize-1, max)){
            return redPacket;
        }

        return perRedPackage(remainMoney, remainSize, max);
    }

    //核对剩余金额
    private static boolean checkRemainMoney(BigDecimal remain, int num, BigDecimal max) {
        BigDecimal remainPer = remain.divide(BigDecimal.valueOf(num), BigDecimal.ROUND_DOWN);
        return remainPer.compareTo(max) < 0 && remainPer.compareTo(MIN_PACKAGE) > 0 ? true:false;
    }

    static BigDecimal divideMoney(BigDecimal divided, int divisor){
        return divided.divide(BigDecimal.valueOf(divisor), BigDecimal.ROUND_DOWN);
    }

    public static void main(String[] args) {
        List list = splitRedPackage(BigDecimal.valueOf(200l), 10);
        System.out.println(list);
    }
}
