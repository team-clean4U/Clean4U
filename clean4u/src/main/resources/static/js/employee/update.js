document.addEventListener("DOMContentLoaded", () => {
   const form = document.getElementById("updateEmployeeForm");
   if (!form) return;

   form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const formData = new FormData(form);
      const data = {
          name: formData.get("name"),
          email: formData.get("email")
      }

      const password = formData.get("password");
      if (password) {
          data.password = password;
      }

      try {
          const response = await fetch("/api/v1/employees/me", {
              method: 'PUT',
              headers: {
                  "Content-Type": "application/json"
              },
              credentials: "include",
              body: JSON.stringify(data)
          });

          if (response.ok) {
              alert("회원 정보가 수정되었습니다.");
              window.location.href = "/dashboard";
          }
      } catch (error) {
          console.error("Error:", error);
          alert("수정 중 오류가 발생했습니다.");
      }
   });
});