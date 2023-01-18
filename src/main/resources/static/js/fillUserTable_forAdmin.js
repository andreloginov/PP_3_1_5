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




async function deleteUser(id) {
    let urlDelete = `http://localhost:8080/api/users/${id}`;
    await fetch(urlDelete, {
        method: 'DELETE',
    });
    let response = await fetch(url);

        let data = await response.json();
        alert('Already get');
        await getArrayUsers(url)
            .then(value => fillTable(value));
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
        toFill += `<button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal"
                                    onclick="deleteUser(${data[index].id})"> Test
                            </button>`;
        toFill += "</td>";

        toFill += "</tr>";
    }

    document.getElementsByTagName('tbody').item(0).innerHTML = toFill;
}






getArrayUsers(url)
    .then(value => fillTable(value));



