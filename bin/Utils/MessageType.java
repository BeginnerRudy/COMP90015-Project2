package Utils;
/*
* This Enum class defines the MessageType of the join operation on the server side.
*
* */
public enum MessageType {
    DUPLICATE_NAME, // The username is duplicated
    REFUSED_JOIN, // The user is refused to join by the manager
    SUCCESS_JOIN, // The user has joined successfully
    UNKNOWN_FAILURE // The join has failed by other failure
}
