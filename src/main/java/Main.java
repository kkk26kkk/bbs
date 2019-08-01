import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		int n = sc.nextInt(); // ����
		int x = sc.nextInt(); // �� ����
		int[] arr = new int[n];
		for(int i=0; i<arr.length; i++) {
			arr[i] = sc.nextInt();
		}
		
		List<Integer> list = new ArrayList<>();
		
		for(int a : arr) {
			if(a < x) 
				list.add(a);
		}
		
		for(int i=0; i<list.size(); i++) {
			System.out.print(list.get(i) + " ");
		}
		
		sc.close();
	}
}
