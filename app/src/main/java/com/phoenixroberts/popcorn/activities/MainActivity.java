package com.phoenixroberts.popcorn.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;

import com.phoenixroberts.popcorn.AppSettings;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.data.DataService;
import com.phoenixroberts.popcorn.data.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.dialogs.DialogService;
import com.phoenixroberts.popcorn.dialogs.Dialogs;
import com.phoenixroberts.popcorn.dialogs.StatusDialog;
import com.phoenixroberts.popcorn.fragments.MovieGridFragment;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;

import java.util.List;


public class MainActivity extends AppCompatActivity implements IDataServiceListener {
    private Menu m_Menu;
    private StatusDialog m_StatusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataServiceBroadcastReceiver.getInstance().addListener(this);
        Toolbar toolbarInstance = (Toolbar)findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarInstance);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new MovieGridFragment()).commit();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager!=null) {
                    ActionBar actionBar = getSupportActionBar();
                    if(actionBar!=null) {
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            actionBar.setDisplayHomeAsUpEnabled(true);
                        } else {
                            actionBar.setDisplayHomeAsUpEnabled(false);
                        }
                    }
                }
            }
        });
        if(savedInstanceState==null) {
            String apiKey = DataService.getInstance().getAPIKey();
            if(TextUtils.isEmpty(apiKey)) {
                promptUserForAPIKey();
            }
            else {
                LoadData();
            }
        }
    }

    public void promptUserForAPIKey() {
        DialogService.getInstance().DisplayTextInputDialog(new Dialogs.TextInputDialogData(this,
                "API Key Entry",
                "Ok",
                "Cancel",
                "Please enter your Movie DB API Key\n(Version 3 format).",
                (eventArgs) -> {
                    //On ok event handler
                    Dialogs.IDialogTextChangedEventData textInputEventArgs = (Dialogs.IDialogTextChangedEventData)eventArgs;
                    //                            Toast.makeText(getActivity(),textInputEventArgs.getText(), Toast.LENGTH_SHORT).show();
                    AppSettings.set(AppSettings.Settings.APKI_Key, textInputEventArgs.getText());
                    DataService.getInstance().setAPIKey(textInputEventArgs.getText());
                    LoadData();
                },
                null,       //On cancel
                null));     //On text changed
    }

    public void LoadData() {
        m_StatusDialog = new StatusDialog(new StatusDialog.ShowStatusRequest(this, true, "Loading",
                StatusDialog.MaskType.Black, true));
        m_StatusDialog.showDialog();
        DataService.getInstance().fetchListDiscoveryData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataServiceBroadcastReceiver.getInstance().removeListener(this);
    }

    @Override
    public void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i) {
        //If the status dialog is displayed close it
        m_StatusDialog.dismissDialog();
    }



    public enum LeadStatus {

        Identified(0),

        Qualifying(1),

        Dispatched(2),

        Enroute(3),

        InProgress(4),

        Active(5),

        DeadLead(-3),

        TurnedDownLocalLead(-2),

        CancelledLead(-1);

        private int m_Value;
        private static java.util.HashMap<Integer, LeadStatus> mappings;

        private static java.util.HashMap<Integer, LeadStatus> getMappings() {
            if (mappings == null)
            {
                synchronized (LeadStatus.class)
                {
                    if (mappings == null)
                    {
                        mappings = new java.util.HashMap<Integer, LeadStatus>();
                    }
                }
            }
            return mappings;
        }
        public int getValue()
        {
            return m_Value;
        }
        public static LeadStatus forValue(int value)
        {
            return getMappings().get(value);
        }
        private LeadStatus(int value) {
            m_Value=value;
            getMappings().put(value, this);
        }

    }

    public class LeadDashboardsResponse {

        public List<LeadDashboard> PriorityResponderLeads;

        public List<MentorDashboard> MentorLeads;

        private boolean m_MentorDataExists;

        public boolean getMentorDataExists() {
            return m_MentorDataExists;
        }


        public void setMentorDataExists(boolean value) {
            m_MentorDataExists = value;
        }

    }

    public class MentorDashboard extends LeadDashboard {

        private Integer m_FranchiseStateProvinceId;

        private Integer m_FranchiseCountryId;

        public Integer getFranchiseStateProvinceId() {
            return m_FranchiseStateProvinceId;
        }


        public void setFranchiseStateProvinceId(Integer value) {
            m_FranchiseStateProvinceId = value;
        }


        public Integer getFranchiseCountryId() {
            return m_FranchiseCountryId;
        }


        public void setFranchiseCountryId(Integer value) {
            m_FranchiseCountryId = value;
        }


    }




    public class LeadDashboard {

        private Integer m_LeadId;

        private Integer m_FranchiseOperationId;

        private Integer m_DispatchedToFranchise;

        private String m_DispatchedFranchiseName;

        private String m_DispatchedFranchiseState;

        private String m_OrganizationName;

        private String m_ClientName;

        private String m_PhoneNumber;

        private String m_DateCreated;

        private String m_Address1;

        private String m_Address2;

        private String m_City;

        private Integer m_StateProvince;

        private String m_PostalCode;

        private String m_PriorityResponder;

        private Integer m_LeadSource;

        private Integer m_AlertCount;

        private LeadStatus m_Status;

        private Integer m_StructureTypeId;

        private Integer m_DefaultCapaMentorId;

        private Integer m_ActiveCapaMentorId;

        private boolean m_UnviewedChanges;

        public Integer getLeadId() {
            return m_LeadId;
        }


        public void setLeadId(Integer value) {
            m_LeadId = value;
        }


        public Integer getFranchiseOperationId() {
            return m_FranchiseOperationId;
        }


        public void setFranchiseOperationId(Integer value) {
            m_FranchiseOperationId = value;
        }


        public Integer getDispatchedToFranchise() {
            return m_DispatchedToFranchise;
        }


        public void setDispatchedToFranchise(Integer value) {
            m_DispatchedToFranchise = value;
        }


        public String getDispatchedFranchiseName() {
            return m_DispatchedFranchiseName;
        }


        public void setDispatchedFranchiseName(String value) {
            m_DispatchedFranchiseName = value;
        }


        public String getDispatchedFranchiseState() {
            return m_DispatchedFranchiseState;
        }


        public void setDispatchedFranchiseState(String value) {
            m_DispatchedFranchiseState = value;
        }


        public String getOrganizationName() {
            return m_OrganizationName;
        }


        public void setOrganizationName(String value) {
            m_OrganizationName = value;
        }


        public String getClientName() {
            return m_ClientName;
        }


        public void setClientName(String value) {
            m_ClientName = value;
        }


        public String getPhoneNumber() {
            return m_PhoneNumber;
        }


        public void setPhoneNumber(String value) {
            m_PhoneNumber = value;
        }


        public String getDateCreated() {
            return m_DateCreated;
        }


        public void setDateCreated(String value) {
            m_DateCreated = value;
        }


        public String getAddress1() {
            return m_Address1;
        }


        public void setAddress1(String value) {
            m_Address1 = value;
        }


        public String getAddress2() {
            return m_Address2;
        }


        public void setAddress2(String value) {
            m_Address2 = value;
        }


        public String getCity() {
            return m_City;
        }


        public void setCity(String value) {
            m_City = value;
        }


        public Integer getStateProvince() {
            return m_StateProvince;
        }


        public void setStateProvince(Integer value) {
            m_StateProvince = value;
        }


        public String getPostalCode() {
            return m_PostalCode;
        }


        public void setPostalCode(String value) {
            m_PostalCode = value;
        }


        public String getPriorityResponder() {
            return m_PriorityResponder;
        }


        public void setPriorityResponder(String value) {
            m_PriorityResponder = value;
        }


        public Integer getLeadSource() {
            return m_LeadSource;
        }


        public void setLeadSource(Integer value) {
            m_LeadSource = value;
        }


        public Integer getAlertCount() {
            return m_AlertCount;
        }


        public void setAlertCount(Integer value) {
            m_AlertCount = value;
        }


        public LeadStatus getStatus() {
            return m_Status;
        }


        public void setStatus(LeadStatus value) {
            m_Status = value;
        }


        public Integer getStructureTypeId() {
            return m_StructureTypeId;
        }


        public void setStructureTypeId(Integer value) {
            m_StructureTypeId = value;
        }


        public Integer getDefaultCapaMentorId() {
            return m_DefaultCapaMentorId;
        }


        public void setDefaultCapaMentorId(Integer value) {
            m_DefaultCapaMentorId = value;
        }


        public Integer getActiveCapaMentorId() {
            return m_ActiveCapaMentorId;
        }


        public void setActiveCapaMentorId(Integer value) {
            m_ActiveCapaMentorId = value;
        }


        public boolean getUnviewedChanges() {
            return m_UnviewedChanges;
        }


        public void setUnviewedChanges(boolean value) {
            m_UnviewedChanges = value;
        }


    }



}
