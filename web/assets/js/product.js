async function loadProduct() {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {
        const productId = parameters.get("id");

        const response = await fetch("LoadSingleProduct?id=" + productId);

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {

            if (response.ok) {
                const json = await response.json();
                console.log(json);
                console.log(json.product);
                console.log(json.productList);

                const product = json.product;
                const productList = json.productList;

                //loading images
                document.getElementById("mainImg").src = "product-images/" + product.id + "/image1.png";
                for (let x = 1; x <= 5; x++) {
                    document.getElementById("galleryImg" + x).src = "product-images/" + product.id + "/image" + x + ".png";
                    document.getElementById("galleryImg" + x + "Div").setAttribute("data-image", "product-images/" + product.id + "/image" + x + ".png");
                }

                //loading other details
                document.getElementById("title").innerHTML = product.title;
                document.getElementById("pageTitle").innerHTML = product.title;
                document.getElementById("breadcrumbTitle").innerHTML = product.title;

                if (product.qty == 0) {
                    document.getElementById("inStockSpan").classList.add("hide");
                    document.getElementById("outOfStockSpan").classList.remove("hide");
                    document.getElementById("addToCartButton").setAttribute("disabled", true);
                    document.getElementById("buyNowButton").setAttribute("disabled", true);
                }

                document.getElementById("addToCartButton").addEventListener(
                        "click",
                        e => {
                            addToCart(product.id, document.getElementById("quantityInput").value);
                        }
                );
                document.getElementById("buyNowButton").addEventListener(
                        "click",
                        e => {
                            //buy now function
                        }
                );

                document.getElementById("price").innerHTML = "Rs." + product.price;
                document.getElementById("categoryName").innerHTML = product.subCategory.category.name;
                document.getElementById("subCategoryName").innerHTML = product.subCategory.name;
                document.getElementById("productDescription").innerHTML = product.description;

                //load related products
                let productCardHtml = document.getElementById("productCard");
                document.getElementById("productCardView").innerHTML = "";

                json.productList.forEach(product => {
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

                });

            }

        } else {
            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }

}