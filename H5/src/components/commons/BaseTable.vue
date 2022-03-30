<template>
  <table class="table table-hover table-nowrap">
    <thead class="table-light">
      <tr>
        <th scope="col">File Name</th>
        <th scope="col">Upload Date</th>
        <th scope="col">progress</th>
        <th scope="col">status</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <div class="d-flex align-items-center">
            <h5>file1</h5>
          </div>
        </td>
        <td>23-01-2022</td>
        <td>
          <div class="d-flex align-items-center">
            <span class="me-2">100%</span>
            <div>
              <div class="progress" style="width: 100px">
                <div
                  class="progress-bar bg-success"
                  role="progressbar"
                  aria-valuenow="100"
                  aria-valuemin="0"
                  aria-valuemax="100"
                  style="width: 100%"
                ></div>
              </div>
            </div>
          </div>
        </td>
        <td>
          <span class="badge badge-lg badge-dot">
            <i class="bg-success"></i>Finish
          </span>
        </td>
        <td class="text-end">
          <a href="#" class="btn btn-sm btn-neutral">View</a>
          <button
            type="button"
            class="btn btn-sm btn-square btn-neutral text-danger-hover"
          >
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
      <tr>
        <td>
          <div class="d-flex align-items-center">
            <h5>file2</h5>
          </div>
        </td>
        <td>23-01-2022</td>
        <td>
          <div class="d-flex align-items-center">
            <span class="me-2">80%</span>
            <div>
              <div class="progress" style="width: 100px">
                <div
                  class="progress-bar bg-warning"
                  role="progressbar"
                  aria-valuenow="80"
                  aria-valuemin="0"
                  aria-valuemax="100"
                  style="width: 80%"
                ></div>
              </div>
            </div>
          </div>
        </td>
        <td>
          <span class="badge badge-lg badge-dot">
            <i class="bg-warning"></i>In progress
          </span>
        </td>
        <td class="text-end">
          <a href="#" class="btn btn-sm btn-neutral">View</a>
          <button
            type="button"
            class="btn btn-sm btn-square btn-neutral text-danger-hover"
          >
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
      <tr>
        <td>
          <div class="d-flex align-items-center">
            <h5>file3</h5>
          </div>
        </td>
        <td>23-01-2022</td>
        <td>
          <div class="d-flex align-items-center">
            <span class="me-2">4%</span>
            <div>
              <div class="progress" style="width: 100px">
                <div
                  class="progress-bar bg-danger"
                  role="progressbar"
                  aria-valuenow="4"
                  aria-valuemin="0"
                  aria-valuemax="100"
                  style="width: 4%"
                ></div>
              </div>
            </div>
          </div>
        </td>
        <td>
          <span class="badge badge-lg badge-dot">
            <i class="bg-danger"></i>Project at risk
          </span>
        </td>
        <td class="text-end">
          <a href="#" class="btn btn-sm btn-neutral">View</a>
          <button
            type="button"
            class="btn btn-sm btn-square btn-neutral text-danger-hover"
          >
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
      <tr>
        <td>
          <div class="d-flex align-items-center">
            <h5>file4</h5>
          </div>
        </td>
        <td>23-01-2022</td>
        <td>
          <div class="d-flex align-items-center">
            <span class="me-2">0%</span>
            <div>
              <div class="progress" style="width: 100px">
                <div
                  class="progress-bar bg-warning"
                  role="progressbar"
                  aria-valuenow="0"
                  aria-valuemin="0"
                  aria-valuemax="100"
                  style="width: 0%"
                ></div>
              </div>
            </div>
          </div>
        </td>
        <td>
          <span class="badge badge-lg badge-dot">
            <i class="bg-secondary"></i>Not started
          </span>
        </td>
        <td class="text-end">
          <a href="#" class="btn btn-sm btn-neutral">View</a>
          <button
            type="button"
            class="btn btn-sm btn-square btn-neutral text-danger-hover"
          >
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>
export default {
  name: "BaseTable",
};
</script>

<style>
</style>