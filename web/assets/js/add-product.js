var subCategoryList;
const popup = Notification();

async function loadCriteria() {
    const response = await fetch(
            "LoadCriteria"
            );

    if (response.ok) {
        const json = await response.json();
        const categoryList = json.categoryList;
        subCategoryList = json.subCategoryList;
        const conditionList = json.conditionList;

        loadSelect("categorySelect", categoryList);
        loadSelect("subCategorySelect", subCategoryList);
        loadSelect("conditionSelect", conditionList);

    } else {
        console.log("error");
    }

}

function loadSelect(selectTagId, list) {
    const selectTag = document.getElementById(selectTagId);

    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item.name;
        selectTag.appendChild(optionTag);
    });

}

function updateSubCategory() {
    let subCategorySelect = document.getElementById("subCategorySelect");
    subCategorySelect.length = 1;

    let selectedCategoryId = document.getElementById("categorySelect").value;

    subCategoryList.forEach(subCategory => {
        if (subCategory.category.id == selectedCategoryId) {
            let optionTag = document.createElement("option");
            optionTag.value = subCategory.id;
            optionTag.innerHTML = subCategory.name;
            subCategorySelect.appendChild(optionTag);
        }
    });

}

async function addProduct() {
    const files = pond.getFiles();

    if (files.length < 5) {
        popup.error({
            message: "Please select upto 5 images!"
        });
    } else if (files.length > 5) {
        popup.error({
            message: "You can only select 5 images!"
        });
    } else if (files.length == 5) {

        const categorySelectTag = document.getElementById("categorySelect");
        const subCategorySelect = document.getElementById("subCategorySelect");
        const titleTag = document.getElementById("title");
        const descriptionTag = document.getElementById("description");
        const conditionSelectTag = document.getElementById("conditionSelect");
        const priceTag = document.getElementById("price");
        const quantityTag = document.getElementById("quantity");
        const imageTag = document.getElementById("imageUploader");

        const data = new FormData();
        data.append("categoryId", categorySelectTag.value);
        data.append("subCategoryId", subCategorySelect.value);
        data.append("title", titleTag.value);
        data.append("description", descriptionTag.value);
        data.append("conditionId", conditionSelectTag.value);
        data.append("price", priceTag.value);
        data.append("quantity", quantityTag.value);

        for (var i = 1; i <= files.length; i++) {
            data.append("image" + i, files[i - 1].file);
        }

        const response = await fetch(
                "ProductListing",
                {
                    method: "POST",
                    body: data
                }
        );

        if (response.ok) {
            const json = await response.json();

            if (json.success) {

                categorySelectTag.value = 0;
                subCategorySelect.length = 1;
                titleTag.value = "";
                descriptionTag.value = "";
                conditionSelectTag.value = 0;
                priceTag.value = "";
                quantityTag.value = 1;
                pond.removeFiles();

                popup.success({
                    title: "Great!",
                    message: json.content
                });

            } else {
                popup.error({
                    title: "Oops...",
                    message: json.content
                });
            }

        } else {
            popup.error({
                title: "Uh ohh",
                message: "Something went wrong. Please try again"
            });
        }
    }
}