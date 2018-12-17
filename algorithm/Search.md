# Search

## BinarySearch

> Limit：Sorted Array

```java
public int binarySearch(int[] x, int target, int length) {
    int low = o, high = length - 1;
    while(low <= high) {
        int mid = (low + high) / 2;
        if(x[mid] == target) return mid;
        if(x[mid] > target) high = mid - 1;
        else low = mid + 1;
    }
    return low;
} 
```


