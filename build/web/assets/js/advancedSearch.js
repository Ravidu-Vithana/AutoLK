const popup = Notification();

let totalProductList;
let totalProductCount;

async function loadfilteringdata() {

    const respone = await fetch(
            "LoadAdvanceSearchFilters"
            );
    if (respone.ok) {

        const json = await respone.json();
        const categoryList = json.categoryList;
        subCategoryList = json.subCategoryList;
        const conditionList = json.conditionList;
        const productList = json.productList;

        console.log(categoryList);
        console.log(subCategoryList);
        console.log(productList);
        console.log(json.productCount);

        loadSelect("categorySelect", categoryList);
        loadSelect("subCategorySelect", subCategoryList);
        totalProductList = productList;
        totalProductCount = json.productCount;
        pagesCount = Math.ceil(totalProductCount / productsPerPageNo);
        loadProduct();

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

function resetFilters() {
    //reset slider
    $("#slider-range").slider("option", "values", [0, 500000]);
    $("#amount").val("Rs0 - Rs500000");
    document.getElementById("categorySelect").value = 0;
    document.getElementById("subCategorySelect").value = 0;
    document.getElementById("SortBy").value = 0;
    document.getElementById("allRadio").checked = true;

    filterResults();

}

var paginationPageNumber = 1;
var productsPerPageNo = 2;

async function filterResults() {

    let categorySelect = document.getElementById("categorySelect");
    let subCategorySelect = document.getElementById("subCategorySelect");
    let sortBySelect = document.getElementById("SortBy");

    let startValue = $("#slider-range").slider("values", 0);
    let endValue = $("#slider-range").slider("values", 1);

    let allRadio = document.getElementById("allRadio");
    let brandNewRadio = document.getElementById("brandNewRadio");
    let usedRadio = document.getElementById("usedRadio");

    let condition = "all";

    if (brandNewRadio.checked) {
        condition = "brandNew";
    } else if (usedRadio.checked) {
        condition = "used";
    }

    let data = {
        categoryId: categorySelect.value,
        subCategoryId: subCategorySelect.value,
        startPrice: startValue,
        endValue: endValue,
        sortBy: sortBySelect.value,
        condition: condition,
    };

    let response = await fetch(
            "AdvancedSearch",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        let json = await response.json();
        console.log(json);
        if (json.success) {

            totalProductList = json.productList;
            totalProductCount = json.productCount;
                pagesCount = Math.ceil(totalProductCount / productsPerPageNo);
            loadProduct();

        } else {
            popup.error({
                message: json.content
            });
        }

    } else {
        popup.error({
            message: "Error occured. Please try again later."
        });
    }

}

let productCardHtml = document.getElementById("productCard");

function loadProduct() {
    document.getElementById("productCardView").innerHTML = "";

    loadPagination();

    if (totalProductList) {

        let paginationCountHandle = 1;
        let firstResultNumber = ((paginationPageNumber - 1) * productsPerPageNo) + 1;
        let lastResultNumber = paginationPageNumber * productsPerPageNo;

        totalProductList.forEach(product => {

            if (paginationCountHandle >= firstResultNumber && paginationCountHandle <= lastResultNumber) {
                let productCardCloneHtml = productCardHtml.cloneNode(true);

                productCardCloneHtml.querySelector("#productCardLink1").href = "product.html?id=" + product.id;
                productCardCloneHtml.querySelector("#productCardPrimaryImg").src = "product-images/" + product.id + "/image1.png";
                productCardCloneHtml.querySelector("#productCardHoverImg").src = "product-images/" + product.id + "/image2.png";
                productCardCloneHtml.querySelector("#productCardCartButton").addEventListener(
                        "click",
                        e => {
                            addToCart(product.id, 1);
                        }
                );

                productCardCloneHtml.querySelector("#productCardWishlistButton").addEventListener(
                        "click",
                        e => {
                            //add to wishlist function
                        }
                );
                productCardCloneHtml.querySelector("#productCardTitle").innerHTML = product.title;
                productCardCloneHtml.querySelector("#productCardTitle").href = "product.html?id=" + product.id;
                productCardCloneHtml.querySelector("#productCardPrice").innerHTML = "Rs." + product.price;
                document.getElementById("productCardView").appendChild(productCardCloneHtml);
            }
            paginationCountHandle++;

        });

    } else {
        popup.error({
            message: "No Products To Show"
        });
    }

}

let paginationView = document.getElementById("paginationView");
let paginationPreviousBtn = document.getElementById("paginationPreviousBtn");
let paginationNumberBtn = document.getElementById("paginationNumberBtn");
let paginationNextBtn = document.getElementById("paginationNextBtn");
let pagesCount = 0;

function loadPagination() {

    paginationView.innerHTML = "";
    paginationView.appendChild(paginationPreviousBtn);

    for (let x = 1; x <= pagesCount; x++) {
        let paginationNumberBtnClone = paginationNumberBtn.cloneNode(true);
        paginationNumberBtnClone.innerHTML = `<a href="#">${x}</a>`;

        let currentLoop = x;
        
        if(x == paginationPageNumber){
            paginationNumberBtnClone.classList.add("active");
        }

        paginationNumberBtnClone.addEventListener(
                "click",
                e => {
                    goToPage(currentLoop);
                }
        );
        paginationView.appendChild(paginationNumberBtnClone);
    }

    paginationView.appendChild(paginationNextBtn);

}

function goToPreviousPage() {
    if (paginationPageNumber > 1) {
        paginationPageNumber--;
        loadProduct();
    }
}

function goToNextPage() {
    if (paginationPageNumber < pagesCount) {
        paginationPageNumber++;
        loadProduct();
    }
}

function goToPage(pageno) {
    paginationPageNumber = pageno;
    loadProduct();
}