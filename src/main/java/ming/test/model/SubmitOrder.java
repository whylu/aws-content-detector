package ming.test.model;

public class SubmitOrder {
    private Integer id;
    private String content;
    private Long submitTime;
    private Boolean isDanger;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }

    public Boolean getDanger() {
        return isDanger;
    }

    public void setDanger(Boolean danger) {
        isDanger = danger;
    }
}
