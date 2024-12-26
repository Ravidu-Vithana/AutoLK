const popup = Notification();

async function register() {
    const user_dto = {
        first_name: document.getElementById("FirstName").value,
        last_name: document.getElementById("LastName").value,
        mobile: document.getElementById("CustomerMobile").value,
        email: document.getElementById("CustomerEmail").value,
        password: document.getElementById("CustomerPassword").value
    };

    const response = await fetch(
            "SignUp",
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
            window.location = "verify-account.html";
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