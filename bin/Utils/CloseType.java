package Utils;
/*
* This Enum class defines 4 different types of closing the whiteboard .
*
* */
public enum CloseType {
    KICKED, // mean the user has been kicked out by the manager
    MANAGER_CLOSE, // the manager close the whiteboard
    SERVER_CLOSE, // the server close the whiteboard
    SELF_CLOSE // the user close the whiteboard by itself
}
