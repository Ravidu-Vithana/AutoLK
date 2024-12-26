const popup = Notification();

async function login() {
    const user_dto = {
        email: document.getElementById("CustomerEmail").value,
        password: document.getElementById("CustomerPassword").value
    };

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }

            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.success) {
            window.location = "index.html";
        } else {
            if (json.content === "Unverified") {
                window.location = "verify-account.html";
            } else {
                popup.error({
                    message: json.content
                });
            }
        }

    } else {
        popup.error({
            message: "Error occured. Please try again later."
        });
    }

}