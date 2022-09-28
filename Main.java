package diverse;

import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static int steps = 0;
	
	public static void main(String args []) {
		
		ArrayList <Integer> lis = new ArrayList<>(100000);
		
		for (int i = 0; i < 100000; i += 1) {
			lis.add(i);
		}
		
		long startTime1 = System.nanoTime();
		binarySearch(lis, 0);
		long endTime1 = System.nanoTime();
		long duration1 = (endTime1 - startTime1);
		System.out.println(duration1/1000000);
		
		long startTime = System.nanoTime();
		linSearch(lis, 87266);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println(duration);
		
		long startTime2 = System.nanoTime();
		jumpSearch(lis, 87266);
		long endTime2 = System.nanoTime();
		long duration2 = (endTime2 - startTime2);
		System.out.println(duration2);
		
	}
	
	public static void binarySearch (List<Integer> list, int num) {
		int index = list.size()/2;
		if (list.size() == 0 || list.get(list.size()-1)<num) {
			System.out.println("No match was found");
		}
		
		else if(list.get(index) > num) {
			steps++;
			binarySearch(list.subList(0, index), num);
			
		}
		
		else if(list.get(index) < num) {
			steps++;
			binarySearch(list.subList(index, list.size()), num);
			
		}
		
		else if(list.get(index)==num){
			System.out.println("Item found!");
			System.out.println("Number of steps " + steps);
		}
		
	}
	
	public static void linSearch (List<Integer> list, int num) {
		
		for(int i : list) {
			if (i == num) {
				System.out.println("Found item");
				break;
			}
			
		}
		
	}
	
	public static void jumpSearch (List<Integer> list, int num) {

		double f = Math.sqrt(list.size());
		
		int m = (int) Math.round(f);
		
			for(int i = 0; i < list.size(); i += m) {
				if (list.get(i) == num) {
					System.out.println("Found");
					break;
				}
				else if (list.get(i) > num) {
					i -= m;
					m = 1;
				}
			}
		}
	}
