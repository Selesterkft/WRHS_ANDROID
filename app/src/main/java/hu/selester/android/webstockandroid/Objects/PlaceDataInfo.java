package hu.selester.android.webstockandroid.Objects;

public class PlaceDataInfo {

    private String virtualWHName;
    private String rentnerName;
    private String itemNo;
    private String itemDescription;
    private String expiryDate;
    private String stockValue;
    private String reservedStockValue;

    public PlaceDataInfo(String virtualWHName, String rentnerName, String itemNo, String itemDescription, String expiryDate, String stockValue, String reservedStockValue) {
        this.virtualWHName = virtualWHName;
        this.rentnerName = rentnerName;
        this.itemNo = itemNo;
        this.itemDescription = itemDescription;
        this.expiryDate = expiryDate;
        this.stockValue = stockValue;
        this.reservedStockValue = reservedStockValue;
    }

    public String getVirtualWHName() {
        return virtualWHName;
    }

    public void setVirtualWHName(String virtualWHName) {
        this.virtualWHName = virtualWHName;
    }

    public String getRentnerName() {
        return rentnerName;
    }

    public void setRentnerName(String rentnerName) {
        this.rentnerName = rentnerName;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStockValue() {
        return stockValue;
    }

    public void setStockValue(String stockValue) {
        this.stockValue = stockValue;
    }

    public String getReservedStockValue() {
        return reservedStockValue;
    }

    public void setReservedStockValue(String reservedStockValue) {
        this.reservedStockValue = reservedStockValue;
    }

    @Override
    public String toString() {
        return "PlaceDataInfo{" +
                "virtualWHName='" + virtualWHName + '\'' +
                ", rentnerName='" + rentnerName + '\'' +
                ", itemNo='" + itemNo + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", stockValue='" + stockValue + '\'' +
                ", reservedStockValue='" + reservedStockValue + '\'' +
                '}';
    }
}
