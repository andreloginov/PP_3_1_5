let url = 'http://localhost:8080/api/users';
/*-------------- start user service ----------------------  */
// ----- here we interact with the database -------------

async function getArrayUsers() {
    let response = await fetch('http://localhost:8080/api/users');
    if (response.ok) {
        let data = await response.json();
        console.log(data);
        return data;
    } else {
        alert("HTTP error: " + response.status)
    }
}

async function getSingleUserById(id) {
    let response = await fetch(`http://localhost:8080/api/users/${id}`)
    if (response.ok) {
        let data = await response.json();
        console.log(data);
        return data;
    } else {
        alert("HTTP error: " + response.status);
    }
}

async function deleteUserById(id) {
    let response = await fetch(`http://localhost:8080/api/users/${id}`, {
        method: 'delete'
    });
    alert(`Method deleteUserById with ID ${id} is already finished`);
}

/*-------------- end user service ----------------------  */



async function fillTable(data) {
    let toFill = "";
    for (let index = 0; index < data.length; index++) {
        toFill += "<tr>";
        toFill += "<td>" + data[index].id + "</td>";
        toFill += "<td>" + data[index].name + "</td>";
        toFill += "<td>" + data[index].surName + "</td>";
        toFill += "<td>" + data[index].email + "</td>";
        toFill += "<td>" + data[index].age + "</td>";
        toFill += "<td>";
        for (let role of data[index].roles) {
            toFill += role.name + ' ';
        }
        toFill += "</td>";
        toFill += "<td>";
        toFill += `<button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal"
                                    data-bs-target='#delete${data[index].id}'> Delete
                            </button>`;
        toFill += "</td>";
        toFill += "<td>";
        toFill += `<button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#exampleModal"
        data-bs data-bs-whatever="${data[index].id}">Delete</button>`;
        toFill += "</td>";
        toFill += "</tr>";
    }
    document.getElementsByTagName('tbody').item(0).innerHTML = toFill;
}



// all users display
getArrayUsers()
    .then(data => fillTable(data));

// --- перехватчик для кнопки submit ---

// const applicantForm = document.getElementById('deleteUser');
// alert(applicantForm.id);
// applicantForm.addEventListener('submit', handleFormSubmit);
//
//
// function serializeForm(formNode) {
//     const { elements } = formNode;
//
//     const data = Array.from(elements)
//         .filter((item) => !!item.name)
//         .map((element) => {
//             const { name, value } = element;
//
//             return {name, value};
//         });
//     console.log(data);
// }
//
// async function handleFormSubmit(event) {
//     event.preventDefault();
//     console.log('Sending!');
//     serializeForm(applicantForm);
// }

// ------ end ----------

// ----- перехватчик before starting modal для модального окна ----
deleteModalCatcher().then(r => console.log('ff'));

async function deleteModalCatcher() {
    const exampleModal = document.getElementById('exampleModal')
    alert(exampleModal)
    exampleModal.addEventListener('show.bs.modal', event => {
        // button that triggered the modal
        const button = event.relatedTarget
        // extract info from data-bs* attributes
        const recipient = button.getAttribute('data-bs-whatever')

        const modalBodyInputs = exampleModal.getElementsByTagName('input');
        const modalBodySelector = document.getElementById('roleEditUser');
        getSingleUserById(recipient)
            .then((user) => {
                modalBodyInputs.namedItem('idShow').placeholder = user.id;
                modalBodyInputs.namedItem('id').value = user.id;
                modalBodyInputs.namedItem('name').placeholder = user.name;
                modalBodyInputs.namedItem('surName').placeholder = user.surName;
                modalBodyInputs.namedItem('age').placeholder = user.age;
                modalBodyInputs.namedItem('email').placeholder = user.email;
                console.log(modalBodySelector);
                let options = "";
                if (user.roles.length > 1) {
                    modalBodySelector.innerHTML = '<option disabled>ADMIN</option> ' +
                        '<option disabled>USER</option>';
                } else {
                    modalBodySelector.innerHTML = '<option disabled>ADMIN</option>';
                }
            });
    })

    // --- перехватчик для кнопки submit ---

    const applicantForm = document.getElementById('deleteUser1');
    alert(applicantForm.id);
    applicantForm.addEventListener('submit', handleFormSubmit);




    function serializeForm(formNode) {
        const {elements} = formNode;

        const data = Array.from(elements)
            .filter((item) => !!item.name)
            .map((element) => {
                const {name, value} = element;

                return {name, value};
            });
        console.log(data);
        return data;
    }

    async function handleFormSubmit(event) {
        event.preventDefault();
        console.log('Sending!');
        let data = serializeForm(applicantForm);
        data.forEach(item => {
            if (item.name === 'id') {
                alert("IT WORKS")
                alert(item.value)
                deleteUserById(item.value).then(() => getArrayUsers().then(value => fillTable(value)));
            }
        })

        // close the modal window
        const modal = bootstrap.Modal.getInstance(exampleModal);
        modal.hide();
        exampleModal.addEventListener('hidden.bs.modal', () => {
            modal.dispose();
        }, {once:true});


    }
// ------ end ----------
}















