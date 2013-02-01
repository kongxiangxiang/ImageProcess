package tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class OTSU  {

	public static double findThreshold(int[] array) {

		double arrayLength = array.length;
		double maxVariance = 0;
		double threshold = 0;
		Map<Double,Double> map = new HashMap();
	
		for (double T = 1; T < arrayLength; T += 0.5) {

			double n0 = 0;
			double n1 = 0;
			double u0 = 0;
			double u1 = 0;
			double w0 = 0;
			double w1 = 0;

			for (int i = 0; i < arrayLength; i++) {
					if (array[i] <= T) {
						u0 = u0 + array[i];
						n0 = n0 + 1;
					} else if (array[i] > T){
						u1 = u1 + array[i];
					}

			}

			n1 = arrayLength - n0;
			
			if(n1 == 0 || n0 == 0){
				continue;
			}

			w0 = n0 / (arrayLength);
			w1 = n1 / (arrayLength);
			u0 = u0 / (n0);
			u1 = u1 / (n1);

			double variance = w1 * w0 * (u1 - u0) * (u1 - u0);
//			System.out.println("w1 = " + w1 + " w0 = " + w0);
			System.out.println("T = " + T + " variance = " + variance);
			if (variance > maxVariance) {
				maxVariance = variance;
				threshold = T;
			}

		}

		return threshold;
	}
	
	public static void main(String[] args) throws InstantiationException, Exception {
		
		int[] array = new int[1005];
		Random r = new Random();
		
		double sum = 0;
		for (int i = 1000; i < array.length; i++) {
			array[i] = i + 100;
		}
		
		for (int i = 0; i <= 500; i++) {
			array[i] = 0;
		}
		
		for (int i = 500; i <= 1000; i++) {
			array[i] = 1;
		}
		
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
			System.out.println(array[i]);
		}
		
		double threshold =  findThreshold(array);
		System.out.println("sum = " + sum / array.length);
		System.out.println("threshold = " + threshold);
	}
}
