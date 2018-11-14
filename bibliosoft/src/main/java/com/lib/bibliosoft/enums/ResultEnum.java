package com.lib.bibliosoft.enums;

/**
 * @Author: 毛文杰
 * @Description: Manage the error code and information uniformly
 * @Date: Created in 2:04 PM. 9/28/2018
 * @Modify By:
 */
public enum ResultEnum {
    UNKNOWN_ERROR(-1, "unknown error"),
    SUCCESS(0, "success"),
    FAILED(-2,"failed"),
//    TOO_SMALL(100, "价格太低"),
//    TOO_LARGE(101, "价格太高"),
    ADD_BOOK_FAILED(102, "add-book failed!"),
    ADD_BOOK_SUCCESS(103, "add-book successful!"),
    ALSO_HAS_TYPE_ERROR(104, "already has this type!"),
    BORROW_BOOK_ERROR(105, "borrow failed!"),
    CANNOT_BORROW(101, "the reader status is OFF, can't borrow books!"),
    HAVE_NO_BOOKS(106, "no books on the shelf!"),
    BORROW_BOOK_SUCCESS(107, "borrow success!"),
    BOOK_ALREADY_DEL(108, "this book has been deleted!"),
    BOOK_DEL_SUCCESS(109, "the book was deleted successfully!"),
    BOOK_DEL_FAILED(110, "the book is not allowed to be deleted!"),
    READER_DEL_FAILED(111, "deletion of the reader failed, the arrears were not paid!"),
    READER_DEL_CANCEL(112, "this reader has books that are not returned and cannot be deleted!"),
    READER_DEL_SUCCESS(113, "remove readers successfully!"),
    CANNOT_DEL_READER(114, "the status is OFF, indicating that it has been deleted!"),
    RETURN_BOOK_SUCCESS(115, "return success!"),
    RETURN_BOOK_PAY(116, "you have already owed a fee, please pay in time"),
    BOOK_ALREADY_RETURN(117, "the book has been returned, please do not repeat the operation!"),
    DOUBAN_ERROR(118,"an error in parsing data from the Douban API. Please go to the manual add-book page! <a style='color:red' href='bookadd_detail'>click me go！</a>"),
    NOT_EXIST(119,"book does not exist! please enter again."),
    BORROW_RECORD_NOT_EXIST(119,"this book doesn't have a borrow record!  please enter again."),
    READER_NOT_EXIST(120, "reader does't exist! please enter again."),
    CAN_NOT_DEL_POS(121,"there are other books in this location, so you can't delete the position!"),
    DEL_POS_SUCCESS(122,"location deleted successfully!"),
    PAY_FINE_SUCCESS(123, "you have successfully paid the fine."),
    ;
    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
