let url = 'http://localhost:8080/api/users';

async function getArrayUsers(url) {
    let response = await fetch(url);
    if (response.ok) {
        let data = await response.json();
        console.log(data);
        return data;
    } else {
        alert("HTTP error: " + response.status)
    }
}

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
        toFill += `<button type="button" className="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal"
        data-bs data-bs-whatever="${data[index].id}">Открыть модальное окно для @mdo</button>`;
        toFill += "</td>";
        toFill += "</tr>";
    }
    document.getElementsByTagName('tbody').item(0).innerHTML = toFill;
}



// all users display
getArrayUsers(url)
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

// ----- перехватчик для модального окна ----

const exampleModal = document.getElementById('exampleModal')
alert(exampleModal)
exampleModal.addEventListener('show.bs.modal', event => {
    // button that triggered the modal
    const button = event.relatedTarget
    // extract info from data-bs* attributes
    const recipient = button.getAttribute('data-bs-whatever')
    alert(recipient)
    alert('EUUUUUUUUUUUUUUUUUUUUUUUUUUU')

    const modalTitle = exampleModal.querySelector('.modal-title')
    const modalBodyInput = exampleModal.querySelector('.modal-body input')
    const modalBodyInputs = exampleModal.getElementsByTagName('input');
    for (let elem of modalBodyInputs) {
        alert(elem);
        alert('bebra');
        elem.value = 'bebra';
    }



    modalTitle.textContent = `New message to ${recipient}`
    modalBodyInput.value = recipient


})














