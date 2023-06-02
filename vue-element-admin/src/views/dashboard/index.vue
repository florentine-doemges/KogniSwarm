<template>
  <div className="dashboard-container">
    <admin-dashboard/>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'
import adminDashboard from './admin'

export default {
  name: 'Dashboard',
  components: {adminDashboard},
  computed: {
    ...mapGetters([
      'roles'
    ])
  }
}
</script>
