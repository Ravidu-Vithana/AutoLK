const popup = Notification();

async function verifyAccount() {

    const dto = {
        verification: document.getElementById("verificationCode").value,
    };

    const response = await fetch(
            "Verification",
            {
                method: "POST",
                body: JSON.stringify(dto),
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