



import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "title",
            "servings",
            "ingredients",
            "instructions"
    })

    public class RequestBodyJson {

        @JsonProperty("title")
        private String title;
        @JsonProperty("servings")
        private Integer servings;
        @JsonProperty("ingredients")
        private List<String> ingredients;
        @JsonProperty("instructions")
        private String instructions;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

        @JsonProperty("title")
        public String getTitle() {
            return title;
        }

        @JsonProperty("title")
        public void setTitle(String title) {
            this.title = title;
        }

        @JsonProperty("servings")
        public Integer getServings() {
            return servings;
        }

        @JsonProperty("servings")
        public void setServings(Integer servings) {
            this.servings = servings;
        }

        @JsonProperty("ingredients")
        public List<String> getIngredients() {
            return ingredients;
        }

        @JsonProperty("ingredients")
        public void setIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
        }

        @JsonProperty("instructions")
        public String getInstructions() {
            return instructions;
        }

        @JsonProperty("instructions")
        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

