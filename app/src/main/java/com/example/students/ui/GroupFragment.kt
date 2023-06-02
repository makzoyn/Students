package com.example.students.ui


import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.students.R
import com.example.students.data.Group
import com.example.students.data.Student
import com.example.students.databinding.FragmentGroupBinding
import com.example.students.models.GroupViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val  GROUP_TAG = "GroupFragment"
class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    val binding
        get() = _binding!!


    companion object {
        private var id : Long = -1
        private  var _group: Group?=null
        fun newInstance(id: Long): GroupFragment{
            GroupFragment()
            this.id=id
            return GroupFragment()
        }
        val getFacultyID
            get() = id
        val getGroup
            get() = _group
    }

    private lateinit var viewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentGroupBinding.inflate(inflater, container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider (this).get(GroupViewModel::class.java)
        viewModel.setFacultyID(getFacultyID)

        CoroutineScope(Dispatchers.Main).launch {
            val f = viewModel.getFaculty()
            callbacks?.setTitle(f?.name ?: "UNKNOWN")
        }

        viewModel.faculty.observe(viewLifecycleOwner){
            updateUI(it)
        }

    }
    private fun showTabOptionsDialog(group: Group) {
        val options = arrayOf("Изменить", "Удалить")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Выберите опцию")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    showEditDialog(group)
                }
                1 -> {
                    commitDeleteDialog(group)
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showEditDialog(group: Group){//создание диалогового окна
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)//подсоединение html
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editTextTextPersonName) as EditText//элементы управления в макете
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView//элементы управления в макете
        builder.setTitle("Редактирование")//устанавливаемм заголовок
        nameInput.setText(group.name)
        tvInfo.text ="Наименование группы"
        builder.setPositiveButton(getString(R.string.commit)){_, _ ->//обработчик нажатия ок
            val s = nameInput.text.toString()//получение значения из поля
            if (s.isNotBlank()){
                CoroutineScope(Dispatchers.Main). launch {
                    viewModel.editGroup(s, group)
                }//вызов метода нф в репозитории
            }
        }
        builder.setNegativeButton(R.string.cancel, null)
        val alert = builder.create()
        alert.show()
    }

    private fun commitDeleteDialog(group: Group) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить группу ${group.name} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            CoroutineScope(Dispatchers.Main). launch {
                viewModel.deleteGroup(group)
            }
            Toast.makeText(requireContext(), "Факультет успешно удалён.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private var tabPosition : Int =0

    private fun updateUI (groups : List<Group>){
        binding.tabGroup.clearOnTabSelectedListeners()
        binding.tabGroup.removeAllTabs()

        binding.faBtnNewStudent.visibility= if((groups.size) > 0) {
            binding.faBtnNewStudent.setOnClickListener {
                callbacks?.showStudent(groups.get(tabPosition).id!!, null)
            }
            View.VISIBLE
        }
        else View.GONE

        for (i in 0 until (groups.size ?: 0)){
            binding.tabGroup.addTab(binding.tabGroup.newTab().apply { text= i.toString() })
        }

        val adapter = GroupPageAdapter(requireActivity(), groups)
        binding.vpGroup.adapter=adapter
        TabLayoutMediator(binding.tabGroup, binding.vpGroup, true, true){
                tab,pos -> tab.text = groups[pos].name
        }.attach()
        if (tabPosition < binding.tabGroup.tabCount){
            binding.tabGroup.selectTab(binding.tabGroup.getTabAt(tabPosition))
            if (groups.size > 0){
                _group = groups[tabPosition]
            }
        }
        else{
            binding.tabGroup.selectTab(binding.tabGroup.getTabAt(tabPosition - 1))
            if (groups.size > 0){
                _group = groups[tabPosition - 1]
            }
        }


        binding.tabGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition=tab?.position!!
                _group= groups[tabPosition]
                viewModel.loadStudents(groups[tabPosition].id!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tabPosition=tab?.position!!
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tabPosition=tab?.position!!
            }
        })

        val tabLayout = binding.tabGroup
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabView = tab?.view
            tabView?.setOnLongClickListener {
                val group = getGroup
                if (group != null) {
                    showTabOptionsDialog(group)
                }
                true
            }
        }
        binding.tabGroup.selectTab(binding.tabGroup.getTabAt(tabPosition))
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val groups: List<Group>):
        FragmentStateAdapter(fa){
        override fun getItemCount(): Int {
            return (groups.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment {
            return  GroupListFragment(groups[position])
        }


    }


    //интерфейс для изменения title приложения на университет
    interface Callbacks{
        fun setTitle(_title: String)
        fun showStudent(groupID: Long, student: Student?)
    }


    var callbacks : Callbacks? =null

    override fun onAttach(context: Context){
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onDetach(){
        callbacks=null
        super.onDetach()
    }


}