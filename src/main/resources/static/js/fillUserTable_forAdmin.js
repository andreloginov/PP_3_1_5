let url = 'http://localhost:8080/api/users';

async function getArrayUsers(url) {
    let response = await fetch(url);
    alert('EE');
    if (response.ok) {
        let data = await response.json();
        console.log(data);
        alert('OK');
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
        toFill += "</tr>";
    }
    document.getElementsByTagName('tbody').item(0).innerHTML = toFill;
}

// all users display
getArrayUsers(url)
    .then(data => fillTable(data));



const applicantForm = document.getElementById('deleteUser');
alert(applicantForm.id);
applicantForm.addEventListener('submit', handleFormSubmit);

// заявитель



function serializeForm(formNode) {
    const { elements } = formNode;

    const data = Array.from(elements)
        .filter((item) => !!item.name)
        .map((element) => {
            const { name, value } = element;

            return {name, value};
        });
    console.log(data);
}

async function handleFormSubmit(event) {
    event.preventDefault();
    console.log('Sending!');
    serializeForm(applicantForm);
}














