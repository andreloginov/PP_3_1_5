let url = 'http://localhost:8080/api/users';

async function getRoles() {
    const response = await fetch('http://localhost:8080/api/getRoles')
    if (response.ok) {
        let data = await response.json();
        console.log(data)
        return data;
    } else {
        alert(`Error to get the role list. Status: ${response.status}`)
    }
}

// the function below returns an array of roles
function getSelectValues(select) {
    let result = [];
    let options = select && select.options;
    let opt;

    for (let i = 0, iLen=options.length; i < iLen; i++) {
        opt = options[i];

        if (opt.selected) {
            result.push(opt.value || opt.text);
        }
    }
    console.log('Selected values:')
    console.log(result)
    return result;
}

async function closeModalWindow(updateModal) {
    const modal = bootstrap.Modal.getInstance(updateModal);
    modal.hide();
    updateModal.addEventListener('hidden.bs.modal', () => {
        modal.dispose();
    }, {once:true});
}

/*-------------- start user service ----------------------  */
// ----- here we interact with the database -------------

async function getArrayUsers() {
    let response = await fetch('http://localhost:8080/api/users');
    if (response.ok) {
        return await response.json();
    } else {
        alert("HTTP error(не удалось получить пользователей): " + response.status)
    }
}

async function getSingleUserById(id) {
    let response = await fetch(`http://localhost:8080/api/users/${id}`)
    if (response.ok) {
        return await response.json();
    } else {
        alert("HTTP error: " + response.status);
    }
}

async function deleteUserById(id) {
    await fetch(`http://localhost:8080/api/users/${id}`, {
        method: 'delete'
    });
    alert(`Method deleteUserById with ID ${id} is already finished`);
}

async function userPostOrPutRequest(data, selectedValues, method) {
    let user = {
        id: data.id,
        name: data.name,
        age: data.age,
        surName: data.surName,
        email: data.email,
        password: data.password,
        passwordConfirm: data.passwordConfirm,
        roles: [
            {
                /*
                    * ID 1 is an admin role
                    * ID 2 is a user role
                    * name will be undefined if it's unselected
                */
                "id": null,
                "name": null,
            },
            {
                "id": null,
                "name": null,
            }
        ]
    }
    if (selectedValues.length > 1) {
        user.roles[0].id = 1;
        user.roles[0].name = selectedValues[0];

        user.roles[1].id = 2;
        user.roles[1].name = selectedValues[1];
    } else if (selectedValues.length === 1) {
        user.roles[0].id = selectedValues[0] === "ROLE_ADMIN" ? 1 : 2;
        user.roles[0].name = selectedValues[0];
        user.roles.pop();
    }


    console.log('user.roles:')
    console.log(user)
    console.log('___________________')

    return await fetch('http://localhost:8080/api/users', {
        method: method == null ? 'PUT' : method,
        headers: {'Content-Type': 'application/json;charset=utf-8'},
        body: JSON.stringify(user)
    });
}

/*-------------- end user service ----------------------  */


async function fillTable(data) {
    if (data == null) {
        data = await getArrayUsers()
    }
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
        toFill += `<button type="button" class="btn btn-info btn-sm" data-bs-toggle="modal"
                                    data-bs-target='#updateModal' data-bs-whatever="${data[index].id}"> Edit
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
    .then(data => fillTable(data)).then();


// ----- перехватчик before starting modal для модального окна ----
deleteModalCatcher().then(r => console.log('Delete script is successfully loaded'));

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
        let data = serializeForm(applicantForm);
        data.forEach(item => {
            if (item.name === 'id') {
                alert("IT WORKS")
                alert(item.value)
                deleteUserById(item.value).then(() => getArrayUsers().then(value => fillTable(value)));
            }
        })

        // close the modal window
        await closeModalWindow(exampleModal);


    }
// ------ end ----------
}


/*
    *
    *
    * ----------------------------------- editing an existing user ----------------------------------
    *
    *
*/

updateModal().then(r => console.log('Update modal is successfully loaded'));

async function updateModal() {
    const roleArray = await getRoles();
    const updateModal = document.getElementById('updateModal')

    updateModal.addEventListener('show.bs.modal', event => {
        // button that triggered the modal
        const button = event.relatedTarget
        // extract info from data-bs* attributes
        const idAttribute = button.getAttribute('data-bs-whatever')

        const modalBodyInputs = updateModal.getElementsByTagName('input');
        const modalBodySelector = document.getElementById('roleEditUser1');
        getSingleUserById(idAttribute)
            .then((user) => {
                modalBodyInputs.namedItem('idShow').value = user.id;
                modalBodyInputs.namedItem('id').value = user.id;
                modalBodyInputs.namedItem('name').value = user.name;
                modalBodyInputs.namedItem('surName').value = user.surName;
                modalBodyInputs.namedItem('age').value = user.age;
                modalBodyInputs.namedItem('email').value = user.email;
                modalBodyInputs.namedItem('passwordConfirm').value = user.passwordConfirm;
                modalBodyInputs.namedItem('password').value = user.password;
                roleArray.forEach(option =>
                    modalBodySelector.add(
                        new Option(option.name, option.name)
                    ));
            });

    })

    // --- перехватчик для кнопки submit ---

    const applicantForm = document.getElementById('userFormUpdate');
    applicantForm.addEventListener('submit', handleFormSubmit);

    async function handleFormSubmit(event) {
        event.preventDefault();
        const selectedValues = getSelectValues(applicantForm.getElementsByTagName('select')[0])

        let data1 = new FormData(event.target);

        let value = Object.fromEntries(data1.entries());

        await userPostOrPutRequest(value, selectedValues)
            .then(response => response.ok ? fillTable()
                    .then(() => closeModalWindow(updateModal))
                    .then(() => alert('data changed successfully'))
                : alert('Enter a correct data'))
    }
}

/* ---------------------------------- end the part of editing ---------------------------------*/


/*
    *
    *
    * ----------------------------------- creating new user ----------------------------------
    *
    *
*/

createUser().then(r => alert(r));

async function createUser() {
    const applicantForm = document.getElementById('newUserForm');
    applicantForm.addEventListener('submit', handleFormSubmit);

    console.log('catch')

    async function handleFormSubmit(event) {
        event.preventDefault();
        const selectedValues = getSelectValues(applicantForm.getElementsByTagName('select')[0])

        let data1 = new FormData(event.target);

        let value = Object.fromEntries(data1.entries());

        await userPostOrPutRequest(value, selectedValues, 'POST')
            .then(response => response.ok ? fillTable()
                    .then(() => {
                        const newUserTab = document.getElementById('nav-home-tab');
                        console.log(newUserTab)
                        newUserTab.click();
                    })
                    .then(() => alert('data changed successfully'))
                : alert('Enter a correct data'))
    }



}

















