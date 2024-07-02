package challenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdListModel {
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private Integer errorCode;
    private String errorMessage;
    private List<Integer> idList;
}
